
@javax.xml.bind.annotation.XmlSchema(
        namespace = "http://example.com/template/1.0/", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
            xmlns = {
                @XmlNs(prefix = "template", namespaceURI = "http://example.com/template/1.0/"),
                @XmlNs(prefix = "nc", namespaceURI = "http://release.niem.gov/niem/niem-core/3.0/"),
                @XmlNs(prefix = "niem-xs", namespaceURI = "http://release.niem.gov/niem/proxy/xsd/3.0/")
            }
)


package com.example.niem.iepd;
import javax.xml.bind.annotation.*;