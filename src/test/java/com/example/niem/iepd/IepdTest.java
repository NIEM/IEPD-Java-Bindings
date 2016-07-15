package com.example.niem.iepd;

import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import javax.xml.bind.*;

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
    JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

    // format output
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    jaxbMarshaller.marshal(factory.createTemplateExchange(templateExchange), file);
    jaxbMarshaller.marshal(factory.createTemplateExchange(templateExchange), System.out);
    return file;
  }

  /**
   * Create and XML Instance with JAXB that is valid to the IEPD
   */
  @Test
  public void testCreateInsance() throws JAXBException, DatatypeConfigurationException {
    TemplateExchangeType exchange = createExchange();
    writeExchange("target/testInstance.xml",exchange);
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
    writeExchange("target/testInstance_createAndRead.xml",createExchange());

    TemplateExchangeType exchange = readExchange("target/testInstance_createAndRead.xml");

    assertThat(exchange.getDateTime(), instanceOf(DateTime.class));
    GregorianCalendar gcal = new GregorianCalendar();
    XMLGregorianCalendar xgcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
    assertThat(exchange.getDateTime().getValue().getYear(), equalTo(xgcal.getYear()));
    assertThat(exchange.getDateTime().getValue().getDay(), equalTo(xgcal.getDay()));

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
