package org.gtri.niem.rest;

import org.gtri.niem.JaxbHelper;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Created by brad on 8/31/16.
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ResponseResolver implements ContextResolver<JAXBContext> {
    private JAXBContext ctx;

    public ResponseResolver() {
        System.out.println("Creating ResponseResolver...");
        System.out.flush();
        try {
            this.ctx = JaxbHelper.createJAXBContext();
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    public JAXBContext getContext(Class<?> type) {
        System.out.println("Returning JAXBContext...");
        System.out.flush();
        return this.ctx;
//        if( JaxbHelper.getJaxbClassNames().contains(type.getName()) ){
//            System.out.println("Getting context for supported TYPE["+type.getName()+"]...");
//            System.out.flush();
//            return this.ctx;
//        }else{
//            System.err.println("Unsupported TYPE["+type.getName()+"]...");
//            System.err.flush();
//            return null;
//        }
    }
}