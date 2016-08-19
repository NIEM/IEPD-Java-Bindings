package org.gtri.jaxb;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.dom4j.Branch
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.Namespace
import org.dom4j.Node
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import org.dom4j.tree.DefaultComment

import java.nio.file.Files;


/**
 * @phase validate
 * @requiresProject true
 */
@Mojo(name="jaxbGenerateConfig")
class JaxbBindingsConfigMojo extends AbstractMojo {

    /**
     * The location of the input IEPD.
     */
    @Parameter(required = true)
    private String iepdDirPath

    /**
     * Used to determine what to do if output already exists that this plugin would need to modify.
     */
    @Parameter
    private Boolean overwritePluginOutput = true;

    /**
     * Where (relative to this project) the prepared files will go, ready to be compiled by XJC.
     */
    @Parameter
    private String outputPath = "../java-bindings/src/main"

    /**
     * Where (relative to this project) the XML instances for testing will go.
     */
    @Parameter
    private String[] instanceXmlPaths = [
            "../java-bindings/src/test/resources/xml",
            "../rest-services/src/test/resources/xml"
    ] as String[];


    /**
     * The user can pass additional jaxb binding files this way.
     */
    @Parameter
    private String[] jaxbBindingFiles = null

    @Parameter(property = "basedir", required = true)
    private File basedir

    void execute() {
        LogHolder.setLog(getLog());
        validateParams();

        getLog().debug("Parsing the IEPD directory...");
        IEPDDirectory iepd = new IEPDDirectory(getFile(this.iepdDirPath));

        // TODO - Right now we simply copy the schemas.  In the future, we may translate them first.
        copyOverSchemaFiles(iepd);

        writePackageInfoJavas(iepd);
        writeJaxbBindingsFile(iepd);

        copyInstances(iepd);
    }

    /**
     * Responsible for copying over all of the XML instances from the iepd to the configured output path.
     */
    void copyInstances(IEPDDirectory iepd){
        if( iepd.getInstanceFiles() != null && iepd.getInstanceFiles().size() > 0 ){
            for( File instanceFile : iepd.getInstanceFiles() ){
                String relativePath = instanceFile.canonicalPath.replace(iepd.base.canonicalPath + File.separator + "xml" + File.separator, "");

                for( String instanceXmlDirPath : this.instanceXmlPaths ){
                    File instanceXmlDir = getFile(instanceXmlDirPath);
                    File outputFile = new File(instanceXmlDir, relativePath);
                    getLog().info("Copying file[${instanceFile.canonicalPath}] to [${outputFile.canonicalPath}]...")
                    Files.copy(instanceFile.canonicalFile.toPath(), outputFile.canonicalFile.toPath());
                }
            }
        }
    }//end copyInstances()

    /**
     * Responsible for creating the output directores for this IEPD.  This is based on the packages determined by the
     * schemas (each one will need a package-info.java file).
     * <br/><br/>
     * @param iepd
     */
    void writePackageInfoJavas(IEPDDirectory iepd) {
        getLog().debug("Writing Package Infos...");

        List<String> packageNames = iepd.getUniquePackages();
        if( packageNames && !packageNames.isEmpty() ){
            for( String packageName : packageNames ){
                String uri = iepd.getUriForPackageName(packageName);
                File baseDir = getJavaDir();
                String packageSubPath = packageName.replace(".", File.separator);
                File packageDir = new File(baseDir, packageSubPath);
                if( !packageDir.exists() ){
                    packageDir.mkdirs();
                }
                File packageInfoJavaFile = new File(packageDir, "package-info.java");
                if( !packageInfoJavaFile.exists() ){
                    JaxbPackageInfoGenerator.writePackageInfo(packageInfoJavaFile, packageName, uri, iepd.getUriPrefixMapping());
                }

            }
        }else{
            getLog().error("This IEPD does not define any packages!  Cannot create any package info files.")
        }
    }// end writePackageInfoJavas

    /**
     * Performs a simple copy of all the XSD files in the IEPD to the $outputDir/xsd directory.
     */
    void copyOverSchemaFiles(IEPDDirectory iepd){
        getLog().debug("Copying schema files...");
        File xsdDir = getXsdDir();
        for( SchemaInfo schemaInfo : iepd.schemas ){
            File schemaFile = schemaInfo.file;
            String relativePath = schemaFile.canonicalPath.replace(iepd.getBase().canonicalPath + File.separator + "xsd", ".");
            File outputFile = new File(xsdDir, relativePath);
            if( !outputFile.getParentFile().exists() ){
                outputFile.getParentFile().mkdirs();
            }
            getLog().info("Copying file[${schemaFile.canonicalPath}] to [${outputFile.canonicalPath}]...")
            Files.copy(schemaFile.canonicalFile.toPath(), outputFile.canonicalFile.toPath());
        }

    }//end copyOverSchemaFiles

    /**
     * Creates the NIEM JAXB Bindings File based on the information in this IEPD.
     */
    void writeJaxbBindingsFile(IEPDDirectory iepd){
        getLog().debug("Creating JAXB Bindings file(s)...");
        File jaxbOutDir = getJaxbDir();

        File jaxbOutputFile = new File(jaxbOutDir, "generated-bindings.xjb");
        JaxbBindingsGenerator.writeJaxbBindings(jaxbOutputFile, iepd);

        if( this.jaxbBindingFiles != null && this.jaxbBindingFiles.length > 0 ) {
            getLog().debug("Appending data from JAXB Bindings files....")
            SAXReader reader = new SAXReader();
            Document mainJaxbBindings = reader.read(jaxbOutputFile);

            List<File> bindingFiles = []
            for( String s : this.jaxbBindingFiles ){
                File bindingFile = getFile(s);
                if( bindingFile == null ){
                    getLog().error("Cannot find binding file: ${s}, this file will be IGNORED!");
                    continue;
                }
                bindingFiles.add(bindingFile);
            }

            Collections.sort(bindingFiles, {File f1, File f2 -> return f1.getName().compareToIgnoreCase(f2.getName())} as Comparator);

            for( File cur : bindingFiles ){
                getLog().debug("Appending data from JAXB Bindings file [${cur.canonicalPath}]....")
                Document bindingFile = reader.read(cur);
                addNamespaces(mainJaxbBindings, bindingFile);
                mainJaxbBindings.getRootElement().add(new DefaultComment(" STARTING FILE ${cur.name} "))
                List topLevelNodes = bindingFile.getRootElement().selectNodes("/*/*");
                for( Object obj : topLevelNodes ){
                    if( obj instanceof Node ) {
                        Node node = (Node) obj;
                        mainJaxbBindings.getRootElement().add(node.detach());
                    }
                }
                mainJaxbBindings.getRootElement().add(new DefaultComment(" STOPPING FILE ${cur.name} "))
            }

            getLog().debug("Writing back to file ${jaxbOutputFile.canonicalPath}...")
            jaxbOutputFile.delete();
            XMLWriter writer = new XMLWriter(new FileWriter(jaxbOutputFile, false), OutputFormat.createPrettyPrint());
            writer.write(mainJaxbBindings);
            writer.flush();
            writer.close();
        }

    }//end writeJaxbBindingsFile()

    void addNamespaces(Document d1, Document d2){
        List<Namespace> namespaces = collectNamespaces(d2);
        for( Namespace ns : namespaces ){
            if( !hasNamespaceUri([d1.getRootElement().getNamespace()], ns.getURI()) &&
                    !hasNamespaceUri(d1.getRootElement().additionalNamespaces(), ns.getURI())){
                d1.getRootElement().add(ns);
            }
        }
    }

    List<Namespace> collectNamespaces(Document d){
        List allNamespaces = []
        List things = d.getRootElement().selectNodes("//*")
        for( Object obj : things ){
            if( obj instanceof Element ){
                Element e = (Element) obj;
                mergeNamespacelist(allNamespaces, [e.getNamespace()]);
                mergeNamespacelist(allNamespaces, e.additionalNamespaces());
            }
        }
        return allNamespaces;
    }

    void mergeNamespacelist(List l1, List l2){
        if( l2?.size() > 0 ){
            for( Namespace ns : l2 ){
                if( !hasNamespaceUri(l1, ns.getURI()) ){
                    l1.add(ns);
                }
            }
        }
    }

    boolean hasNamespaceUri(List l1, String uri ){
        if( l1?.size() > 0 ){
            for( Namespace ns : l1 ){
                if( ns.getURI().equalsIgnoreCase(uri) ){
                    return true;
                }
            }
        }
        return false;
    }


    File getJavaDir(){
        return new File(getFile(this.outputPath), "java");
    }
    File getXsdDir(){
        return new File(getFile(this.outputPath), "xsd");
    }
    File getJaxbDir(){
        return new File(getFile(this.outputPath), "xjb");
    }
    File getResourcesDir(){
        return new File(getFile(this.outputPath), "resources");
    }

    File getFile(String givenPath){
        File dir = new File(givenPath);
        if( dir.exists() )
            return dir;
        dir = new File(this.basedir, givenPath)
        if( dir.exists() )
            return dir;
        return null;
    }

    void validateParams(){
        getLog().info("Validating parameters: \n"+
            "    iepdDirPath = [${this.iepdDirPath}]\n"+
            "    basedir = [${this.basedir.canonicalPath}]");

        getLog().debug("Checking that ${this.iepdDirPath} exists...");
        File iepdDir = getFile(this.iepdDirPath);
        if( iepdDir == null ){
            getLog().error("Could not find IEPD Directory based on parameter value: "+iepdDirPath);
            throw new FileNotFoundException("Could not find IEPD directory: "+this.iepdDirPath);
        }else if( !iepdDir.isDirectory() ){
            getLog().error("Expecting IEPD Directory '${this.iepdDirPath}' to be a directory!");
            throw new RuntimeException("File '${this.iepdDirPath}' is not a directory, and thus cannot contain an IEPD or schemas.")
        }

        getLog().debug("Checking that ${this.iepdDirPath}/xsd exists...");
        File schemasDir = new File(iepdDir, "xsd");
        if( !schemasDir.exists() ){
            getLog().error("No such directory found ${schemasDir.canonicalPath}, but it is required to exist and contain your XML Schema (xsd) files.")
            throw new FileNotFoundException("No such directory found ${schemasDir.canonicalPath}, but it is required to exist and contain your XML Schema (xsd) files.")
        }else if( !schemasDir.isDirectory() ){
            getLog().error("File '${schemasDir.canonicalPath}' is not a directory, but it must be and contain XSD files.")
            throw new RuntimeException("File '${schemasDir.canonicalPath}' is not a directory, but it must be and contain XSD files.");
        }

        getLog().debug("Checking that ${this.iepdDirPath}/xml exists...");
        File instancesDir = new File(iepdDir, "xml");
        if( !instancesDir.exists() ){
            getLog().warn("Could not find directory ${instancesDir.canonicalPath}.  This means NO TESTS marshalling/unmarshalling XML will be created for your JAXB project!");
            // We don't error here, it's technically OK for them not to have any tests.  Just not advisable.
        }else if( !instancesDir.isDirectory() ){
            getLog().error("File '${schemasDir.canonicalPath}' is not a directory, but it must be and contain XML files.")
            throw new RuntimeException("File '${schemasDir.canonicalPath}' is not a directory, but it must be and contain XML files.");
        }

        getLog().debug("Checking if ${this.outputPath} does not exist...");
        File outputDir = getFile(this.outputPath);
        if( outputDir != null && !this.overwritePluginOutput ){
            getLog().error("Unable to remove directory ${this.outputPath}, since 'overwritePluginOutput' is set to false.");
            throw new RuntimeException("Unable to remove directory ${this.outputPath}, since 'overwritePluginOutput' is set to false.")
        }else if( outputDir?.exists() ) {
            getLog().debug("Removing output directory ${outputDir}...");
            FileUtils.delete(outputDir);
        }
        outputDir.mkdirs();

        getJavaDir().mkdirs();
        getJaxbDir().mkdirs();
        getXsdDir().mkdirs();
        getResourcesDir().mkdirs();

        for( String instanceXmlPath : this.instanceXmlPaths ?: []){
            getLog().debug("Checking if ${instanceXmlPath} does not exist...");
            File instanceXmlDir = getFile(instanceXmlPath);
            if( instanceXmlDir != null && !this.overwritePluginOutput ){
                getLog().error("Unable to remove directory ${instanceXmlDir.canonicalPath}, since 'overwritePluginOutput' is set to false.");
                throw new RuntimeException("Unable to remove directory ${instanceXmlDir.canonicalPath}, since 'overwritePluginOutput' is set to false.")
            }else if( instanceXmlDir?.exists() ) {
                getLog().debug("Removing output directory ${instanceXmlDir}...");
                FileUtils.delete(instanceXmlDir);
            }
            instanceXmlDir.mkdirs();
        }


    }


}