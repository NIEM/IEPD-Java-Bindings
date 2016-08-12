package com.example.niem.iepd;

import io.github.benas.randombeans.api.EnhancedRandom;
import org.gtri.niem.JaxbHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Path("sample")
public class RestService {

  private static Random random = null;
  static {
    random = new Random(Calendar.getInstance().getTimeInMillis());
  }


  /**
   * Generates a random "interesting" class from the JAXB Bundle.  Note that this uses some nasty reflection, you may
   * find it easier to use this code instead:
   *  <code>return ObjectFactory.createXXX( EnhancedRandom.random( instance ) );</code>
   *
   * But that requires compile time dependencies on your generated objects, a luxury we don't have here because we
   * don't know what your classes are going to be ahead of time.
   * <br/><br/>
   * @param id
   * @return
   */
  @GET
  @Path("/random/{id}")
  @Produces(MediaType.APPLICATION_XML)
  public JAXBElement getRandomExample(@PathParam("id") int id) {

    // TODO In theory, giving the same id should generate the same result, not different ones.
    List<String> potentialClasses = JaxbHelper.getNonNIEMClassNames();
    String className = potentialClasses.get(random.nextInt(potentialClasses.size()));
    try {
      Class clz = Class.forName(className);
      Object instance = EnhancedRandom.random(clz);
      String objectFactoryClassName = clz.getPackage().getName() + ".ObjectFactory";
      Class objectFactoryClz = Class.forName(objectFactoryClassName);
      Method staticCreateMethod = objectFactoryClz.getMethod("create"+clz.getName(), clz);
      return (JAXBElement) staticCreateMethod.invoke(null, instance);
    }catch(Throwable t){
      System.err.println(t);
      throw new RuntimeException("An unexpected error occurred.", t);
    }
  }


  @POST
  @Path("/pretty-print")
  @Consumes(MediaType.APPLICATION_XML)
  public Response sendTemplateExchange(JAXBElement jaxbe) throws JAXBException {
//    prettyPrint(jaxbe);
//    String resultString = "Created exchange: "+templateExchange.getDateTime().getValue();
    return Response.status(Response.Status.CREATED).entity("Successful").build();
  }


}