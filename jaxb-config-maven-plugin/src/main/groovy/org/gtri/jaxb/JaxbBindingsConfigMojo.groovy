package org.gtri.jaxb;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter;


/**
 * @phase validate
 * @requiresProject true
 */
@Mojo(name="jaxbGenerateConfig")
class JaxbBindingsConfigMojo extends AbstractMojo {

    @Parameter(required = true)
    private String iepdDirPath

    @Parameter(required = true)
    private String xsdOutputPath

    @Parameter
    private Boolean overwriteXsdOutput = true;

    @Parameter(property = "basedir", required = true)
    private File basedir

    void execute() {
        LogHolder.setLog(getLog());
        validateParams();

        getLog().debug("Parsing the IEPD directory...");
        IEPDDirectory iepd = new IEPDDirectory(new File(this.basedir, this.iepdDirPath));

//        JaxbPackageInfoGenerator packageInfoGenerator = new JaxbPackageInfoGenerator(iepd.getUriPrefixMapping());
//        String packageInfoFileContents = packageInfoGenerator.getFileContents();

//        JaxbPackageInfoGenerator.writeJavaPackageInfo("$basedir/src/main/java/${packageName.replace('.','/')}/package-info.java",packageName,"http://example.com/template/1.0/",namespaceScanner.namespaceMap)
//        JaxbBindingsGenerator.writeJaxbBindings("$basedir/src/main/xjb/generated-bindings.xjb",rootSchema,packageName,basedir,namespaceScanner.schemaLocation)
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

        getLog().debug("Checking if ${this.xsdOutputPath} does not exist...");
        File xsdOutputDir = new File(this.basedir, this.xsdOutputPath);
        if( xsdOutputDir.exists() && !this.overwriteXsdOutput ){
            getLog().error("Unable to remove directory ${xsdOutputDir.canonicalPath}, since 'overwriteXsdOutput' is set to false.");
            throw new RuntimeException("Unable to remove directory ${xsdOutputDir.canonicalPath}, since 'overwriteXsdOutput' is set to false.")
        }else if( xsdOutputDir.exists() ) {
            getLog().debug("Removing output directory ${xsdOutputDir}...");
            FileUtils.delete(xsdOutputDir);
        }
        xsdOutputDir.mkdirs();



    }


}