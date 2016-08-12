package org.gtri.niem;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A class which helps the caller deal with JAXBContext objects based on the compiled schemas.
 */
public class JaxbHelper {

    public static final String BUNDLE_NAME = "org.gtri.niem.jaxb_config";

    private static Boolean BUNDLE_LOCK = Boolean.TRUE;
    private static ResourceBundle CACHED_BUNDLE = null;
    private static ResourceBundle loadBundle() {
        synchronized (BUNDLE_LOCK){
            if( CACHED_BUNDLE == null )
                CACHED_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
        }
        return CACHED_BUNDLE;

    }


    public static List<String> getJaxbPackages(){
        List<String> packages = new ArrayList<>();
        ResourceBundle bundle = loadBundle();
        Integer packageCount = Integer.parseInt(bundle.getString("jaxb.package.count"));
        for( int i = 0; i < packageCount; i++ ){
            packages.add(bundle.getString("jaxb.package."+i));
        }
        return packages;
    }

    public static String buildJAXBInitPackagesString() {
        List<String> packages = getJaxbPackages();
        StringWriter writer = new StringWriter();
        for( int i = 0; i < packages.size(); i++ ){
            writer.append(packages.get(i));
            if( i < (packages.size() - 1) )
                writer.append(":");
        }
        return writer.toString();
    }

    public static JAXBContext createJAXBContext() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(buildJAXBInitPackagesString());
        return jaxbContext;
    }

    public static Marshaller createMarshaller() throws JAXBException {
        return createJAXBContext().createMarshaller();
    }

    public static Unmarshaller createUnmarshaller() throws JAXBException {
        return createJAXBContext().createUnmarshaller();
    }


}