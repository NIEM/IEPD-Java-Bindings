package com.example.niem.iepd;

import io.github.benas.randombeans.api.EnhancedRandom;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

@Path("template")
public class RestService {

  @GET
  @Path("/exchange/{id}")
  @Produces(MediaType.APPLICATION_XML)
  public JAXBElement<TemplateExchangeType> getTemplateExchange(@PathParam("id") int id) {
    return new ObjectFactory().createTemplateExchange(EnhancedRandom.random(TemplateExchangeType.class));
  }


  @POST
  @Path("/exchange")
  @Consumes(MediaType.APPLICATION_XML)
  public Response sendTemplateExchange(TemplateExchangeType templateExchange) throws JAXBException {
    formattedPrint(templateExchange);
    String resultString = "Created exchange: "+templateExchange.getDateTime().getValue();
    return Response.status(Response.Status.CREATED).entity(resultString).build();
  }


  private void formattedPrint(TemplateExchangeType templateExchange) throws JAXBException {
    ObjectFactory factory = new ObjectFactory();
    JAXBContext jaxbContext = JAXBContext.newInstance("com.example.niem.iepd");
    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

    // format and write output
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    jaxbMarshaller.marshal(factory.createTemplateExchange(templateExchange), System.out);

  }
}