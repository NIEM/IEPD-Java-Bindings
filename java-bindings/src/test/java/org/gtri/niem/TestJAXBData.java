package org.gtri.niem;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileFilter;
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
public class TestJAXBData {

    private static final Logger logger = Logger.getLogger(TestJAXBData.class);

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        ArrayList<Object[]> data = new ArrayList<>();
        File xmlDir = new File("./src/test/resources/xml");

        FileFilter xmlFilter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xml");
            }
        };
        ArrayList<File> files = new ArrayList<>();
        populateFromFilter(files, xmlDir, xmlFilter);

        if( files != null && files.size() > 0 ) {
            for (File f : files) {
                data.add(new Object[]{f});
            }
        }else{
            logger.error("Could not find any XML files to test!");
        }

        return data;
    }

    private static void populateFromFilter(List<File> xmlFiles, File dir, FileFilter filter){
        if( dir.isDirectory() ){
            File[] currentXmlFiles = dir.listFiles(filter);
            if( currentXmlFiles != null && currentXmlFiles.length > 0 ){
                for(File f:currentXmlFiles){
                    xmlFiles.add(f);
                }
            }
            File[] subdirs = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            if( subdirs != null && subdirs.length > 0 ){
                for(File subdir:subdirs){
                    populateFromFilter(xmlFiles, subdir, filter);
                }
            }
        }else if( filter.accept(dir) ){
            xmlFiles.add(dir);
        }
    }


    @Before
    public void printStartTest(){
        logger.info("======================================== STARTING TEST ========================================");
    }
    @After
    public void printStoppingTest(){
        logger.info("======================================== STOPPING TEST ========================================\n\n");
    }


    private File xmlFile;
    public TestJAXBData(File xmlFile){
        this.xmlFile = xmlFile;
    }


    @Test
    public void runTest() throws Exception {
        logger.info("Executing JAXB test on XML File["+this.xmlFile.getCanonicalPath()+"]...");

        logger.debug("Creating unmarshaller...");
        Unmarshaller unmarshaller = JaxbHelper.createUnmarshaller();
        assertThat(unmarshaller, notNullValue());

        logger.debug("Unmarshalling "+this.xmlFile);
        Object obj = unmarshaller.unmarshal(this.xmlFile);
        assertThat(obj, notNullValue());

        logger.info("Successfully unmarshalled "+obj.getClass().getName());
        if( obj instanceof JAXBElement ){
            JAXBElement jaxbe = (JAXBElement) obj;
            Object value = jaxbe.getValue();
            assertThat(value, notNullValue());
            logger.info("  JAXBElement is of type: "+value.getClass().getName());
        }

        logger.info("Successfully tested JAXB can unmarshall file "+this.xmlFile.getCanonicalPath());
    }

}
