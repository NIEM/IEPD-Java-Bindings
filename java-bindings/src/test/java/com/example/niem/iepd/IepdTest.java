package com.example.niem.iepd;

import com.example.niem.iepd.TemplateExchangeType;
import gov.niem.release.niem.proxy.xsd._3.DateTime;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import javax.xml.bind.*;

import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;


public class IepdTest {

  /**
   * Random Create JAXB TemplateExchangeType instance for testing
   */
  public TemplateExchangeType createExchangeRandom() throws DatatypeConfigurationException {
    TemplateExchangeType exchange = EnhancedRandom.random(TemplateExchangeType.class);
    return exchange;
  }

  /**
   * Example creating exchange with objects
   */
  public TemplateExchangeType createExchange() throws DatatypeConfigurationException {
    ObjectFactory factory = new ObjectFactory();

    // create root xml element for exchange
    TemplateExchangeType exchange = factory.createTemplateExchangeType();

    // create date value
    GregorianCalendar gcal = new GregorianCalendar();
    XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
    DateTime dateTime = new DateTime();
    dateTime.setValue(xgcal);
    exchange.setDateTime(dateTime);

    return exchange;
  }

  public TemplateExchangeType readExchange(java.lang.String fileName) throws JAXBException {
    File file = new File(fileName);
    JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    JAXBElement<TemplateExchangeType> jaxbExchange = (JAXBElement<TemplateExchangeType>) jaxbUnmarshaller.unmarshal(file);
    TemplateExchangeType exchange = jaxbExchange.getValue();
    System.out.println(exchange);
    return exchange;
  }

  public File writeExchange(java.lang.String fileName, TemplateExchangeType templateExchange) throws JAXBException {
    File file = new File(fileName);
    ObjectFactory factory = new ObjectFactory();
    JAXBContext jaxbContext = JAXBContext.newInstance("com.example.niem.iepd");
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

    // format and write output
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    jaxbMarshaller.marshal(factory.createTemplateExchange(templateExchange), file);
    jaxbMarshaller.marshal(factory.createTemplateExchange(templateExchange), System.out);
    return file;
  }

  /**
   * Create and XML Instance with JAXB that is valid to the IEPD
   */
  @Test
  public void testCreateInstance() throws JAXBException, DatatypeConfigurationException {
    TemplateExchangeType exchange = createExchangeRandom();
    writeExchange("target/testInstanceRandom.xml",exchange);
  }
 

  /**
   * Read an XML Instance with JAXB
   */
  @Test
  public void testReadInstance() throws JAXBException {
    TemplateExchangeType exchange = readExchange("src/test/resources/sample-exchange.xml");

    assertThat(exchange.getDateTime(), instanceOf(DateTime.class));
    assertThat(exchange.getDateTime().getValue().getYear(), equalTo(2014));
  }

  /**
   * Create a test exchange and read it in to JAXB objects
   */
  @Test
  public void testCreateAndRead() throws JAXBException, DatatypeConfigurationException {
    writeExchange("target/testInstance_createAndRead.xml",createExchangeRandom());

    TemplateExchangeType exchange = readExchange("target/testInstance_createAndRead.xml");

    assertThat(exchange.getDateTime(), instanceOf(DateTime.class));
    assertThat(exchange.getDateTime().getValue().getYear(), is(notNullValue()));
    assertThat(exchange.getDateTime().getValue().getDay(), is(notNullValue()));
  }

  /**
   * Read in an exchange into JAXB objects and write it back out
   */
  @Test
  public void testReadAndCreate() throws JAXBException {
    TemplateExchangeType exchange = readExchange("src/test/resources/sample-exchange.xml");

    File file = writeExchange("target/testInstance_readAndCreate.xml",exchange);
    assertThat(file,notNullValue());
    assertThat(file.exists(),is(true));
    assertThat(file.canRead(),is(true));
  }
}
