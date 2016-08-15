package org.gtri.jaxb

import org.dom4j.Document
import org.dom4j.Element
import org.dom4j.Namespace
import org.dom4j.io.SAXReader


/**
 * Created by brad on 8/10/16.
 */
class SchemaInfo {

    Document dom4jDoc;
    String targetNamespace;
    File file;
    Map<String, String> uriToPrefixMappings = [:]
    Map<String, String> prefixToUriMappings = [:]

    public SchemaInfo(File file){
        this.file = file;
        updateContent();
    }

    public void setFile(File f){
        this.file = f;
        updateContent();
    }

    public void updateContent() {
        SAXReader reader = new SAXReader();
        this.dom4jDoc = reader.read(file);
        this.targetNamespace = this.dom4jDoc.getRootElement().selectObject("string(/*[local-name()='schema']/@*[local-name()='targetNamespace'])")
        updateNsPrefixMappings();
    }

    public void updateNsPrefixMappings() {
        List<Element> elements = dom4jDoc.getRootElement().selectNodes("//*");
        for (Element e : elements){
            List<Namespace> namespaces = e.declaredNamespaces();
            for( Namespace ns : namespaces ?: [] ){
                if( !uriToPrefixMappings.containsKey(ns.getURI()) ){
                    uriToPrefixMappings.put(ns.getURI(), ns.getPrefix());
                }
                if( !prefixToUriMappings.containsKey(ns.getPrefix()) ){
                    prefixToUriMappings.put(ns.getPrefix(), ns.getURI());
                }
            }
        }
    }


}
