package org.gtri.jaxb

class JaxbBindingsGenerator {

    static void writeJaxbBindings(String fileName, String rootSchema, String packageName, String relativePath,  List<String> schemaLocation) {
        File file = new File(fileName)
        File dir = new File(file.getParentFile().absolutePath)
        if(!dir.exists()) {
            dir.mkdirs()
        }
        file.write '''\
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<jaxb:bindings
    xmlns:jaxb="http://java.sun.com/xml/ns/jaxb"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/jaxb http://java.sun.com/xml/ns/jaxb/bindingschema_2_0.xsd"
    jaxb:extensionBindingPrefixes="xjc"
    version="2.1">
'''
    schemaLocation.each { location ->
      if(location.contains(rootSchema)) {
        file << "  <jaxb:bindings schemaLocation=\"${'..' + location - relativePath - 'src/main/'}\" node=\"/xsd:schema\">\n"
        file << "    <jaxb:schemaBindings>\n"
        file << "      <jaxb:package name=\"${packageName}\"/>\n"
        file << "    </jaxb:schemaBindings>\n"
        file << "  </jaxb:bindings>\n"
      }
    }

    file << "    <jaxb:globalBindings typesafeEnumMaxMembers=\"1000\"/>\n"
    file << '''
</jaxb:bindings>
'''
    }
}
