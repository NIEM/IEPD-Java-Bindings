
@javax.xml.bind.annotation.XmlSchema(
    namespace = "http://example.com/template/1.0/", elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
    xmlns = {
      @XmlNs(prefix = "appinfo", namespaceURI = "http://release.niem.gov/niem/appinfo/3.0/"),
      @XmlNs(prefix = "ct", namespaceURI = "http://release.niem.gov/niem/conformanceTargets/3.0/"),
      @XmlNs(prefix = "nc", namespaceURI = "http://release.niem.gov/niem/niem-core/3.0/"),
      @XmlNs(prefix = "term", namespaceURI = "http://release.niem.gov/niem/localTerminology/3.0/"),
      @XmlNs(prefix = "structures", namespaceURI = "http://release.niem.gov/niem/structures/3.0/"),
      @XmlNs(prefix = "template", namespaceURI = "http://example.com/template/1.0/"),
      @XmlNs(prefix = "xsi", namespaceURI = "http://www.w3.org/2001/XMLSchema-instance"),
      @XmlNs(prefix = "xs", namespaceURI = "http://www.w3.org/2001/XMLSchema"),
    }
)

package com.example.niem.iepd;
import javax.xml.bind.annotation.*;
