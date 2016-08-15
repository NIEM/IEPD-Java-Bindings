package org.gtri.jaxb

import jaxb_src.NameConverter

/**
 * This class parses the given IEPD directory and finds/maintains relevant and appropriate files.
 * <br/><br/>
 * Created by brad on 8/10/16.
 */
class IEPDDirectory {

    public static final String NS_PREFIX_FILE = "ns-prefix-mappings.xml";
    public static final String NS_PACKAGE_MAPPING_FILE = "ns-package-mappings.xml";


    File base = null;
    List<File> schemaFiles;
    List<SchemaInfo> schemas;
    List<File> instanceFiles;
    Map<String, String> uriPrefixMapping = [:]
    Map<String, String> prefixUriMapping = [:]
    Map<String, String> uriToPackageMapping = [:]

    public IEPDDirectory(File base){
        this.base = base;
        init();
    }

    /**
     * Scans for all unique packages defined by this IEPD.
     */
    public List<String> getUniquePackages(){
        List packages = []
        if( this.uriToPackageMapping && this.uriToPackageMapping.keySet().size() > 0 ){
            for( String uri : this.uriToPackageMapping.keySet() ){
                String packageName = this.uriToPackageMapping.get(uri);
                if( !packages.contains(packageName) ){
                    packages.add(packageName);
                }
            }
        }
        return packages;
    }

    public String getUriForPackageName(String packageName){
        if( this.uriToPackageMapping && this.uriToPackageMapping.keySet().size() > 0 ){
            for( String uri : this.uriToPackageMapping.keySet() ){
                String currentPackageName = this.uriToPackageMapping.get(uri);
                if( packageName.equals(currentPackageName) ){
                    return uri;
                }
            }
        }
        LogHolder.getLog().warn("No URI found for package name: "+packageName);
        return null;
    }

    /**
     * Setter method prevents outside folks from setting the base after construction.  Only the Constructor is the way to
     * do this.
     */
    private void setBase(File f){}

    //==================================================================================================================
    //  Initialization (reading of IEPD directory)
    //==================================================================================================================
    /**
     * Called to initialize the process( read the IEPD directory )
     * <br/><Br/>
     * @param base
     */
    private void init(){
        LogHolder.getLog().debug("Parsing IEPD contents from: "+base.canonicalPath);

        LogHolder.getLog().debug("Searching for XML Schema files...");
        this.schemaFiles = [];
        FileUtils.findFiles(new File(base, "xsd"), this.schemaFiles, FileUtils.XSD_FILE_FILTER);

        LogHolder.getLog().debug("Searching for XML Instance files...");
        this.instanceFiles = [];
        FileUtils.findFiles(new File(base, "xml"), this.instanceFiles, FileUtils.XML_FILE_FILTER);

        File prefixFile = new File(this.base, NS_PREFIX_FILE);
        if( !prefixFile.exists() ){
            LogHolder.getLog().warn("File '${base.canonicalPath}${File.separator}${NS_PREFIX_FILE}' does not exist.  Plugin will scan files to determine missing schema namespace/prefix mappings...");
        }else{
            loadNsPrefixMappings(prefixFile);
        }

        File packageFile = new File(this.base, NS_PACKAGE_MAPPING_FILE);
        if( !packageFile.exists() ){
            LogHolder.getLog().warn("File '${base.canonicalPath}${File.separator}${NS_PACKAGE_MAPPING_FILE}' does not exist.  Plugin will scan files to determine how URIs map to namespaces...");
        }else{
            loadNsPackageMappings(packageFile);
        }

        scanSchemas();
        addMissingNsPrefixMappings();
        addMissingPackageMappings();

    }

    /**
     * Searches through the SchemaInfo classes, adding any missing URI to package mappings.
     */
    private void addMissingPackageMappings(){
        LogHolder.getLog().debug("Scanning for missing ns/package mappings...")
        for( SchemaInfo schema : this.schemas ?: [] ){
            String targetNs = schema.getTargetNamespace();
            if( !this.uriToPackageMapping.containsKey(targetNs) ){
                String generatedPackage = NameConverter.standard.toPackageName(targetNs);
                LogHolder.getLog().warn("URI[${targetNs}] does not have any entry in ${IEPDDirectory.NS_PACKAGE_MAPPING_FILE}.  Using '${generatedPackage}', as generated from the targetNs of schema file ${schema.file.canonicalPath}")
                this.uriToPackageMapping.put(targetNs, generatedPackage);
            }
        }
    }//end addMissingPackageMappings()

    /**
     * Searches through the SchemaInfo classes, adding any missing uri to prefix or prefix to URI mappings that were not specifically set.
     */
    private void addMissingNsPrefixMappings(){
        LogHolder.getLog().debug("Scanning for missing ns/prefix mappings from schemas...");
        for( SchemaInfo schema : this.schemas ?: [] ){
            for( String uri : schema.getUriToPrefixMappings()?.keySet() ?: [] ) {
                if( !this.uriPrefixMapping.containsKey(uri) ){
                    String prefix = schema.getUriToPrefixMappings().get(uri);
                    LogHolder.getLog().warn("URI[${uri}] does not have an entry in ${IEPDDirectory.NS_PREFIX_FILE}.  Using '${prefix}', as read from schema file ${schema.file.canonicalPath}")
                    this.uriPrefixMapping.put(uri, prefix);
                    this.prefixUriMapping.put(prefix, uri);
                }
            }
        }

        for( SchemaInfo schema : this.schemas ?: [] ) {
            for (String prefix : schema.getPrefixToUriMappings()?.keySet() ?: []) {
                if( !this.prefixUriMapping.containsKey(prefix) ){
                    LogHolder.getLog().warn("Found prefix ${prefix} which is not mapped!");
                }
            }
        }

    }//end addMissingNsPrefixMappings()


    private void loadNsPrefixMappings(File file){
        LogHolder.getLog().debug("Reading namespace/prefix mapping file: "+NS_PREFIX_FILE);

        int count = 0;
        def nsPrefixMappings = new XmlSlurper().parse(file);
        nsPrefixMappings.mapping?.each{ mapping ->
            String prefix = mapping["@prefix"]
            String uri = mapping["@uri"]
            if( prefix?.trim()?.length() > 0 && uri?.trim()?.length() > 0 ) {
                LogHolder.getLog().debug("Found defined mapping: " + prefix + " => " + uri);
                uriPrefixMapping.put(uri, prefix);
                prefixUriMapping.put(prefix, uri);
                count++;
            }else{
                LogHolder.getLog().warn("Found bad mapping entry in File[${file.canonicalPath}].  Please do it this way: <mapping prefix=\"...\" uri=\"...\" />")
            }
        }

        LogHolder.getLog().debug("Found ${count} defined ns/prefix mappings!");
    }

    private void loadNsPackageMappings(File file){
        LogHolder.getLog().debug("Reading namespace/package mapping file: "+NS_PACKAGE_MAPPING_FILE);
        int count = 0;
        def nsPackageMappings = new XmlSlurper().parse(file);
        nsPackageMappings.mapping?.each{ mapping ->
            String packageName = mapping["@package"]
            String uri = mapping["@uri"]
            if( packageName?.trim()?.length() > 0 && uri?.trim()?.length() > 0 ) {
                LogHolder.getLog().debug("Found defined mapping: " + packageName + " => " + uri);
                uriToPackageMapping.put(uri, packageName);
                count++;
            }else{
                LogHolder.getLog().warn("Found bad mapping entry in File[${file.canonicalPath}].  Please do it this way: <mapping package=\"...\" uri=\"...\" />")
            }
        }

        LogHolder.getLog().debug("Found ${count} defined ns/package mappings!");

    }


    private void scanSchemas(){
        this.schemas = []
        for( File file : this.schemaFiles ?: []){
            LogHolder.getLog().debug("Processing schema file: "+ file)
            SchemaInfo schemaInfo = new SchemaInfo(file);
            this.schemas.add(schemaInfo);
        }

        // TODO Should we download remotely reference schemas?

    }



}
