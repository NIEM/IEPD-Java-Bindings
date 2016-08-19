package org.gtri.niem;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by brad on 8/18/16.
 */
public abstract class AbstractTest {

    private static final Logger logger = Logger.getLogger(AbstractTest.class);

    protected static FileFilter XML_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xml");
        }
    };
    protected static FileFilter JAVA_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".java");
        }
    };

    protected static FileFilter SUBDIR_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };

    @Before
    public void printStartTest(){
        logger.info("======================================== STARTING TEST ========================================");
    }
    @After
    public void printStoppingTest(){
        logger.info("======================================== STOPPING TEST ========================================\n\n");
    }


    static List<File> generatedJavaFiles = null;

    protected static List<File> getGeneratedJavaFiles(){
        synchronized (logger){
            if( generatedJavaFiles == null ){
                generatedJavaFiles = buildJavaFilesList("./target/generated-sources/jaxb");
            }
            return generatedJavaFiles;
        }
    }

    private static List<File> buildJavaFilesList(String parentPath){
        File file = new File(parentPath);
        List<File> javaFiles = new ArrayList<>();
        populateFromFilter(javaFiles, file, JAVA_FILTER);
        return javaFiles;
    }

    protected static void populateFromFilter(List<File> xmlFiles, File dir, FileFilter filter){
        if( dir.isDirectory() ){
            File[] currentXmlFiles = dir.listFiles(filter);
            if( currentXmlFiles != null && currentXmlFiles.length > 0 ){
                for(File f:currentXmlFiles){
                    xmlFiles.add(f);
                }
            }
            File[] subdirs = dir.listFiles(SUBDIR_FILTER);
            if( subdirs != null && subdirs.length > 0 ){
                for(File subdir:subdirs){
                    populateFromFilter(xmlFiles, subdir, filter);
                }
            }
        }else if( filter.accept(dir) ){
            xmlFiles.add(dir);
        }
    }

}
