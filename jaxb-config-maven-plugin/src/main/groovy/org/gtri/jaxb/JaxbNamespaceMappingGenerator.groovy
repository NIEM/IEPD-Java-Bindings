package org.gtri.jaxb


class JaxbNamespaceMappingGenerator {

    static void writeJavaPackageInfo(String fileName, String packageName, String namespace, Map<String,String> namespaceMap) {
        File file = new File(fileName)
        File dir = new File(file.getParentFile().absolutePath)
        if(!dir.exists()) {
            dir.mkdirs()
        }

        file.write "\n"
        file << "@javax.xml.bind.annotation.XmlSchema(\n"
        file << "    namespace = \"$namespace\", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,\n"
        file << "    xmlns = {\n"
        namespaceMap.each { uri, prefix ->
            file << "      @XmlNs(prefix = \"$prefix\", namespaceURI = \"$uri\"),\n"
        }
        file << "    }\n"
        file << ")\n\n"
        file << "package $packageName;\n"
        file << "import javax.xml.bind.annotation.*;\n"
    }
}
