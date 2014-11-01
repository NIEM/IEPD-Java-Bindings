package com.example.niem.iepd;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;

import com.example.niem.iepd.TemplateExchangeType;
import com.example.niem.iepd.ObjectFactory;

public class IepdTest {

  /**
   * Create JAXB TemplateExchangeType instance for testing
   */
  public TemplateExchangeType createExchange() throws DatatypeConfigurationException {
    // create exchange and populate
    ObjectFactory factory = new ObjectFactory();
    TemplateExchangeType exchange = factory.createTemplateExchangeType();

    GregorianCalendar gcal = new GregorianCalendar();
    XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
    DateTime dateTime = factory.createDateTime();
    dateTime.setValue(xgcal);
    exchange.setDateTime(dateTime);

    return exchange;    
  }

  /**
   * Create and XML Instance with JAXB that is valid to the IEPD
   */
  @Test
  public void testCreateInsance() throws JAXBException, DatatypeConfigurationException {
    ObjectFactory factory = new ObjectFactory();
    TemplateExchangeType exchange = createExchange();
   
    File file = new File("target/instance.xml");
    JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

    // format output
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    jaxbMarshaller.marshal(factory.createTemplateExchange(exchange), file);
    jaxbMarshaller.marshal(factory.createTemplateExchange(exchange), System.out);

    //TODO: validate the generated instance against the schemas in the IEPD
  }
 

  /**
   * Read an XML Instance with JAXB
   */
  @Test
  public void testReadInstance() throws JAXBException {
    File file = new File("src/test/resources/sample-exchange.xml");
    JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class); 
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    JAXBElement<TemplateExchangeType> jaxbExchange = (JAXBElement<TemplateExchangeType>) jaxbUnmarshaller.unmarshal(file);
    TemplateExchangeType exchange = jaxbExchange.getValue();
    System.out.println(exchange);

    assertThat(exchange.getDateTime(), instanceOf(DateTime.class));
    assertThat(exchange.getDateTime().getValue().getYear(), equalTo(2014));
  }
}
