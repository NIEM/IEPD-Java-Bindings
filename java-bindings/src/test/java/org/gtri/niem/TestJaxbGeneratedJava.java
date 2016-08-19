package org.gtri.niem;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * When JAXB finds errors, like naming conflicts, then a "Rest" property is created.  This can lead to all sorts of other
 * issues, where properties are not put on the object.
 * <br/><br/>
 * Created by brad on 8/18/16.
 */
@RunWith(Parameterized.class)
public class TestJaxbGeneratedJava extends AbstractTest {

    private static final Logger logger = Logger.getLogger(TestJaxbGeneratedJava.class);

    @Parameterized.Parameters(name="TestJaxbGeneratedJava: {1}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> data = new ArrayList<>();

        try {
            List<File> files = getGeneratedJavaFiles();
            File base = new File("./target/generated-sources/jaxb");
            if (files != null && files.size() > 0) {
                for (File f : files) {
                    String relativePath = f.getCanonicalPath().replace(base.getCanonicalPath() + File.separator, "");
                    data.add(new Object[]{f, relativePath});
                }
            } else {
                logger.error("Could not find any XML files to test!");
            }
        }catch(Throwable t){

        }

        return data;
    }




    private File xmlFile;
    private String relativePath;
    public TestJaxbGeneratedJava(File xmlFile, String relativePath){
        this.xmlFile = xmlFile;
        this.relativePath = relativePath;
    }


    @Test
    public void testPropertyGeneration() throws Exception {
        if( this.xmlFile.getName().contains("ObjectFactory.java") ){
            logger.info("No need to scan ObjectFactory.");
            return;
        }
        logger.info("Scanning for no rest property...");
        logger.debug("Analyzing java file "+relativePath);
        CompilationUnit cu = JavaParser.parse(this.xmlFile);
        new FieldDeclarationVisitor().visit(cu, null);

    }//end testNoRestProperty


    static class FieldDeclarationVisitor extends VoidVisitorAdapter {

        private static final Logger logger = Logger.getLogger(FieldDeclarationVisitor.class);

        @Override
        public void visit(FieldDeclaration fieldDeclaration, Object arg) {
            assertThat(fieldDeclaration.getVariables().size(), equalTo(1) );
            VariableDeclarator vd = fieldDeclaration.getVariables().get(0);
            assertThat(vd, notNullValue());
            assertThat(vd.getId(), notNullValue());
            String name = vd.getId().getName();
            assertThat(name, notNullValue());
            if( name.startsWith("_") && !name.equals("_case") ){
                Assert.fail("Class contains a property starting with underscore!  Property: "+name);
            }
            if( name.equalsIgnoreCase("rest") ){
                Assert.fail("Class contains a 'rest' property, indicating a name conflict among other things");
            }
            super.visit(fieldDeclaration, arg);
        }

    }


}
