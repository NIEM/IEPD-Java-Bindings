package org.gtri.jaxb

/**
 * Created by brad on 8/10/16.
 */
class FileUtils {

    public static final FileFilter XSD_FILE_FILTER = new FileFilter() {
        @Override
        boolean accept(File pathname) {
            return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xsd");
        }
    }
    public static final FileFilter XML_FILE_FILTER = new FileFilter() {
        @Override
        boolean accept(File pathname) {
            return pathname.isFile() && pathname.getName().toLowerCase().endsWith(".xml");
        }
    }
    public static final FileFilter SUBDIR_FILE_FILTER = new FileFilter() {
        @Override
        boolean accept(File pathname) {
            // TODO We could check to make sure it isn't ignored somehow, to prevent recursion.
            return pathname.isDirectory();
        }
    }

    /**
     * A simple recursive function to find all files matching the given {@link FileFilter} in the directory tree.
     */
    public static void findFiles(File dir, List<File> matchingFiles, FileFilter filter){
        if( dir && dir.isDirectory() ) {
            File[] matches = dir.listFiles(filter);
            if (matches && matches.length > 0) {
                for (File match : matches) {
                    matchingFiles.add(match);
                }
            }
            File[] subdirs = dir.listFiles(SUBDIR_FILE_FILTER);
            if (subdirs && subdirs.length > 0) {
                for (File subdir : subdirs) {
                    findFiles(subdir, matchingFiles, filter);
                }
            }
        }else if( dir ){
            if( filter.accept(dir) ){
                matchingFiles.add(dir);
            }
        }
    }//end findFiles()

    /**
     * Removes the file, recursively if necessary.
     */
    public static void delete(File file){
        if( file.isDirectory() ){
            file.deleteDir();
        }else{
            file.delete();
        }
    }


}
