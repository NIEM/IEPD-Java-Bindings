package org.gtri.jaxb;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter;


@Mojo(name="jaxbGenerateConfig")
class JaxbBindingsConfigMojo extends AbstractMojo {

    @Parameter(property = "jaxbConfig.schemaPath", defaultValue = "src/main/xsd")
    private String schemaPath

    @Parameter(property = "basedir")
    private String basedir

    @Parameter(property = "jaxbConfig.packageName", defaultValue = "com.example.niem.iepd")
    private String packageName

    void execute() {
        getLog().info( "Generate XML package-info.java to specify namesapce prefixes and jaxb bindings file to generate java bindings" )
        getLog().info("Searching for schemas in: $basedir/$schemaPath")

        SchemaNamespaceScanner namespaceScanner = new SchemaNamespaceScanner()
        namespaceScanner.scanForNamespaces("$basedir/$schemaPath")

        JaxbNamespaceMappingGenerator.writeJavaPackageInfo("$basedir/src/main/java/${packageName.replace('.','/')}/package-info.java",packageName,"http://example.com/template/1.0/",namespaceScanner.namespaceMap)
        JaxbBindingsGenerator.writeJaxbBindings("$basedir/src/main/xjb/generated-bindings.xjb",packageName,basedir,namespaceScanner.schemaLocation)
    }
}