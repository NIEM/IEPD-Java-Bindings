package org.gtri.niem;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import javax.xml.bind.*;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A parameterized test, which finds any XML instances from "iepd-source" and runs them end-to-end using JAXB.
 * <br/><br/>
 * Created by brad on 8/9/16.
 */
@RunWith(Parameterized.class)
public class TestJAXBData extends AbstractTest {

    private static final Logger logger = Logger.getLogger(TestJAXBData.class);

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        ArrayList<Object[]> data = new ArrayList<>();
        File xmlDir = new File("./src/test/resources/xml");

        ArrayList<File> files = new ArrayList<>();
        populateFromFilter(files, xmlDir, XML_FILTER);

        if( files != null && files.size() > 0 ) {
            for (File f : files) {
                data.add(new Object[]{f});
            }
        }else{
            logger.error("Could not find any XML files to test!");
        }

        return data;
    }



    private File xmlFile;
    public TestJAXBData(File xmlFile){
        this.xmlFile = xmlFile;
    }


    @Test
    public void runTest() throws Exception {
        logger.info("Executing JAXB test on XML File["+this.xmlFile.getCanonicalPath()+"]...");

        logger.debug("Creating marshaller/unmarshaller...");
        Unmarshaller unmarshaller = JaxbHelper.createUnmarshaller();
        assertThat(unmarshaller, notNullValue());
        Marshaller marshaller = JaxbHelper.createMarshaller();
        assertThat(marshaller, notNullValue());

        logger.debug("Unmarshalling "+this.xmlFile);
        Object obj = unmarshaller.unmarshal(this.xmlFile);
        assertThat(obj, notNullValue());

        Class clz1 = obj.getClass();
        logger.info("Successfully unmarshalled "+obj.getClass().getName());
        if( obj instanceof JAXBElement ){
            JAXBElement jaxbe = (JAXBElement) obj;
            Object value = jaxbe.getValue();
            assertThat(value, notNullValue());
            logger.info("  JAXBElement is of type: "+value.getClass().getName());
            clz1 = value.getClass();
        }

        File marshallToFile = new File("./target/test/xml-instances/"+this.xmlFile.getName());
        marshallToFile.getParentFile().mkdirs();
        FileWriter fileOut = new FileWriter(marshallToFile, false);
        marshaller.marshal(obj, fileOut);
        logger.debug("Successfully marshalled file: "+marshallToFile.getCanonicalPath());

        logger.debug("Unmarshalling the marshalled out file: "+marshallToFile.getCanonicalPath());
        Object obj2 = unmarshaller.unmarshal(marshallToFile);
        assertThat(obj2, notNullValue());

        Class clz2 = obj2.getClass();
        logger.info("Successfully unmarshalled "+obj2.getClass().getName());
        if( obj2 instanceof JAXBElement ){
            JAXBElement jaxbe = (JAXBElement) obj2;
            Object value = jaxbe.getValue();
            assertThat(value, notNullValue());
            logger.info("  JAXBElement is of type: "+value.getClass().getName());
            clz2 = value.getClass();
        }

        // TODO we could somehow generically compare the two which would do more than this does...
        assertThat(clz1, equalTo(clz2));

        logger.info("Successfully tested JAXB can unmarshall file "+this.xmlFile.getCanonicalPath());
    }

}
