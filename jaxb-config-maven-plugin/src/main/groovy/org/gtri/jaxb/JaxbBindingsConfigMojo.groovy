package org.gtri.jaxb;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter

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
        IEPDDirectory iepd = new IEPDDirectory(new File(this.basedir, this.iepdDirPath));

        // TODO - Right now we simply copy the schemas.  In the future, we may translate them first.
        copyOverSchemaFiles(iepd);

        writePackageInfoJavas(iepd);
        writeJaxbBindingsFile(iepd);

    }

    /**
     * Responsible for creating a file which contains information about what was generated.  It can be used to deduce
     * what was in the schemas for the purpose of dynamically loading JAXB, for example.
     */
    void writeJaxbPropertiesFile(IEPDDirectory iepd){
        File resourcesDir = getResourcesDir();

    }

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
        copyOverJaxbFiles(iepd, jaxbOutDir);

        File jaxbOutputFile = new File(jaxbOutDir, "generated-bindings.xjb");
        JaxbBindingsGenerator.writeJaxbBindings(jaxbOutputFile, iepd);
    }//end writeJaxbBindingsFile()

    /**
     * Performs a copy operation for any values found in the jaxbBindingFiles array above.
     */
    void copyOverJaxbFiles(IEPDDirectory iepd, File jaxbOutDir) {
        if( this.jaxbBindingFiles != null && this.jaxbBindingFiles.length > 0 ){
            for( String jaxbFilePath : this.jaxbBindingFiles ){
                File jaxbFile = new File(this.basedir, jaxbFilePath);
                if( jaxbFile.exists() ){
                    File outputFile = new File(jaxbOutDir, jaxbFile.name);
                    getLog().info("Copying file[${jaxbFile.canonicalPath}] to [${outputFile.canonicalPath}]...")
//                    Files.copy(jaxbFile.canonicalFile.toPath(), outputFile.canonicalFile.toPath());
                    getLog().error("Due to an existing ERROR in the jaxb2-maven-plugin, additional xjc files are precluded and will NOT be copied.")
                }
            }
        }else{
            getLog().info("Found no additional jaxb files specified by <jaxbBindingFiles>, not copying over any.")
        }
    }//end copyOverJaxbFiles()

    File getJavaDir(){
        return new File(new File(this.basedir, this.outputPath), "java");
    }
    File getXsdDir(){
        return new File(new File(this.basedir, this.outputPath), "xsd");
    }
    File getJaxbDir(){
        return new File(new File(this.basedir, this.outputPath), "xjb");
    }
    File getResourcesDir(){
        return new File(new File(this.basedir, this.outputPath), "resources");
    }

    void validateParams(){
        getLog().info("Validating parameters: \n"+
            "    iepdDirPath = [${this.iepdDirPath}]\n"+
            "    basedir = [${this.basedir.canonicalPath}]");

        getLog().debug("Checking that ${this.iepdDirPath} exists...");
        File iepdDir = new File(this.basedir, this.iepdDirPath);
        if( !iepdDir.exists() ){
            getLog().error("Could not find IEPD Directory: "+iepdDir.canonicalPath+", based on parameter value: "+iepdDirPath);
            throw new FileNotFoundException("Could not find IEPD directory: "+iepdDir.canonicalPath);
        }else if( !iepdDir.isDirectory() ){
            getLog().error("Expecting IEPD Directory '${iepdDir.canonicalPath}' to be a directory!");
            throw new RuntimeException("File '${iepdDir.canonicalPath}' is not a directory, and thus cannot contain an IEPD or schemas.")
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
            getLog().warn("Could not find directory ${instancesDir.canonicalPath}.  This means NO TESTS will be created for your JAXB project!");
            // We don't error here, it's technically OK for them not to have any tests.  Just not advisable.
        }else if( !instancesDir.isDirectory() ){
            getLog().error("File '${schemasDir.canonicalPath}' is not a directory, but it must be and contain XML files.")
            throw new RuntimeException("File '${schemasDir.canonicalPath}' is not a directory, but it must be and contain XML files.");
        }

        getLog().debug("Checking if ${this.outputPath} does not exist...");
        File outputDir = new File(this.basedir, this.outputPath);
        if( outputDir.exists() && !this.overwritePluginOutput ){
            getLog().error("Unable to remove directory ${outputDir.canonicalPath}, since 'overwritePluginOutput' is set to false.");
            throw new RuntimeException("Unable to remove directory ${outputDir.canonicalPath}, since 'overwritePluginOutput' is set to false.")
        }else if( outputDir.exists() ) {
            getLog().debug("Removing output directory ${outputDir}...");
            FileUtils.delete(outputDir);
        }
        outputDir.mkdirs();

        getJavaDir().mkdirs();
        getJaxbDir().mkdirs();
        getXsdDir().mkdirs();
        getResourcesDir().mkdirs();


    }


}