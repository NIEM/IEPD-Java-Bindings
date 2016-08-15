package org.gtri.niem.rest;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * Created by brad on 8/12/16.
 */
@RunWith(Parameterized.class)
public class InstancePutTest extends AbstractTest {
    private static final Logger logger = Logger.getLogger(InstancePutTest.class);

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


    private File xmlFile;
    public InstancePutTest(File xmlFile){
        this.xmlFile = xmlFile;
    }

    @Test
    public void testInstancePut() throws Exception {
        logger.info("Testing PUT with file: "+xmlFile.getCanonicalPath());

        logger.debug("Calling web service...");
        Response response = target.path("sample/pretty-print")
                .request(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_XML)
                .post(Entity.entity(this.xmlFile, MediaType.APPLICATION_XML));
        assertThat(response.getStatus(), equalTo(Response.Status.CREATED.getStatusCode()));

        String responsetext = response.readEntity(String.class);
        assertThat(responsetext, startsWith("Successfully unmarshalled & marshalled class of type: "));

        logger.info("File["+xmlFile.getCanonicalPath()+"] was successfully 'PUT' onto server.");
    }

}
