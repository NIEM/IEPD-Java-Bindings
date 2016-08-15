package org.gtri.niem.rest;

import io.github.benas.randombeans.api.EnhancedRandom;
import org.gtri.niem.JaxbHelper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.StringWriter;
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


  private static void info(String msg){
    System.out.println(msg);
    System.out.flush();
  }

  private static void error(String msg){
    System.err.println(msg);
    System.err.flush();
  }


  /**
   * Generates a random "interesting" class from the JAXB Bundle.  Note that this uses some nasty reflection, you may
   * find it easier to use this code instead: <Br/>
   *
   *  <code>return ObjectFactory.createXXX( EnhancedRandom.random( instance ) );</code>
   * <Br/><Br/>
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
    info("Successfully called getRandomExample("+id+")");
    // TODO In theory, giving the same id should generate the same result, not different ones.
    List<String> potentialClasses = JaxbHelper.getNonNIEMClassNames();
    if( potentialClasses == null || potentialClasses.size() == 0 ){
      error("No potential classes to serve up!");
      throw new UnsupportedOperationException("No potential classes to serve.  This service only works when 'NIEM' is not found in a class name.");
    }
    String className = potentialClasses.get(random.nextInt(potentialClasses.size()));
    info("  Creating sample Class: "+className);
    try {
      Class clz = Class.forName(className);
      Object instance = EnhancedRandom.random(clz);
      String objectFactoryClassName = clz.getPackage().getName() + ".ObjectFactory";
      Class objectFactoryClz = Class.forName(objectFactoryClassName);
      Method staticCreateMethod = objectFactoryClz.getMethod("create"+(clz.getSimpleName().replace("Type", "")), clz);

      return (JAXBElement) staticCreateMethod.invoke(objectFactoryClz.newInstance(), instance);
    }catch(Throwable t){
      t.printStackTrace(System.err);
      throw new RuntimeException("An unexpected error occurred.", t);
    }
  }


  /**
   * Called to consume anything sent to pretty print.  This particular implementation accepts ANY XML, then uses the
   * JaxbHelper to create an Unmarshaller to get your class, create a marshaller and then pretty print it to the screen.
   *
   * When coding your own version here, you can accept a JAXBElement&lt;YourType&gt; and JAXB will be called automatically
   * based on the ObjectFactory for "YourType".
   * <br/><br/>
   * @param streamSource
   * @return
   * @throws JAXBException
   */
  @POST
  @Path("/pretty-print")
  @Consumes({MediaType.APPLICATION_XML, MediaType.TEXT_XML})
  public Response prettyprint(StreamSource streamSource) throws JAXBException {
    info("Successfully called prettyprint with streamSource...");

    info("Calling unmarshaller on stream source...");
    Object obj = JaxbHelper.createUnmarshaller().unmarshal(streamSource);
    Class type = null;
    if( obj instanceof JAXBElement ){
      JAXBElement jaxbe = (JAXBElement) obj;
      type = jaxbe.getValue().getClass();
    }else{
      type = obj.getClass();
    }
    StringWriter writer = new StringWriter();
    JaxbHelper.createMarshaller().marshal(obj, writer);
    info("UPLOADED XML IS: \n"+writer.toString());

    return Response.status(Response.Status.CREATED).entity("Successfully unmarshalled & marshalled class of type: "+type.getName()).build();
  }


}