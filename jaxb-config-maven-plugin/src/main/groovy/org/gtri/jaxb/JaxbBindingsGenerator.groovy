package org.gtri.jaxb

class JaxbBindingsGenerator {

    static void writeJaxbBindings(String fileName, String packageName, String relativePath,  List<String> schemaLocation) {
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
    xmlns:annox="http://annox.dev.java.net"
    xsi:schemaLocation="http://java.sun.com/xml/ns/jaxb http://java.sun.com/xml/ns/jaxb/bindingschema_2_0.xsd"
    jaxb:extensionBindingPrefixes="xjc annox"
    version="2.1">
'''
    file << "  <jaxb:bindings schemaLocation=\"${'..' + schemaLocation[0] - relativePath - 'src/main/'}\" node=\"/xsd:schema\">\n"
    file << "    <jaxb:schemaBindings>\n"
    file << "      <jaxb:package name=\"$packageName\"/>\n"
    file << "    </jaxb:schemaBindings>\n"
    file << "    <jaxb:globalBindings typesafeEnumMaxMembers=\"1000\"/>\n"

    file << '''
  </jaxb:bindings>
</jaxb:bindings>
'''
    }
}
