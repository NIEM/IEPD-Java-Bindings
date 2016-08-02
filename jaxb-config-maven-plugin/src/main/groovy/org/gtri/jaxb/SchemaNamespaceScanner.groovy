package org.gtri.jaxb

import static groovy.io.FileType.FILES


/**
 * Collect a mapping of namespace uris to prefixes referenced in schemas. Uses the first prefix discovered for each uri
 * if there are multiple different uses. Increments the prefix if there are multiple uris that use the same prefix.
 */
class SchemaNamespaceScanner {

  Map<String,String> namespaceMap
  List<String> schemaLocation

  /**
   * Recursively scans a file path gathering namespace uris and prefixes
   * @param path relative file path to start scanning
   * @return map<uri, prefix> where uri and prefix are strings
   */
  Map<String,String> scanForNamespaces(String path) {
    namespaceMap = new HashMap<>()
    schemaLocation = new ArrayList<>()

    new File(path).eachFileRecurse(FILES) { file ->
      if(file.name.endsWith('.xsd') || file.name.endsWith('.xs')) {
        println "Found schema file: "+ file
        schemaLocation.add(file.absolutePath)

        def regex = ~/xmlns:([a-zA-Z0-9]+)\s*=\s*"([\S]+)"/
        file.eachLine { line ->
          def matcher = regex.matcher(line)
          while(matcher.find()) {
            def prefix = matcher.group(1)
            def uri = matcher.group(2)
            if(!namespaceMap.containsKey(uri)) {
              namespaceMap.put(uri,prefix)
            }
          }
        }
      }
    }
    println "Found namespace values: "
    namespaceMap.each { uri, prefix ->
      println "  $prefix=$uri"
    }
    return namespaceMap
  }
}
