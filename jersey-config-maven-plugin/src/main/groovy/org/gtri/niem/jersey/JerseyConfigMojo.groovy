package org.gtri.niem.jersey

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.Namespace
import org.dom4j.Node
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import org.dom4j.tree.DefaultComment

import java.nio.file.Files

/**
 * @phase validate
 * @requiresProject true
 */
@Mojo(name="jaxbGenerateConfig")
class JerseyConfigMojo extends AbstractMojo {

    /**
     * The location of the input IEPD.
     */
    @Parameter(required = true)
    private String javaBindingsProejctPath;

    /**
     * Used to determine what to do if output already exists that this plugin would need to modify.
     */
    @Parameter
    private Boolean overwritePluginOutput = true;

    /**
     * Where (relative to this project) the prepared files will go, ready to be compiled by XJC.
     */
    @Parameter
    private String outputPath = "./src/main"

    @Parameter(property = "basedir", required = true)
    private File basedir

    void execute() {
        LogHolder.setLog(getLog());
        validateParams();

        getLog().info("Generating a REST Jersey Project...");

    }

    /**
     * Performs validation of the incoming parameters, including assuring that input/output directories exist.
     */
    private void validateParams() {

    }

}