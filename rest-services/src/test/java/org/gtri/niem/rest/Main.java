package org.gtri.niem.rest;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;


public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/app/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        logger.info("Starting the Grizzly server...");
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        String[] packages = null;
        try{
            packages = RestServices.readServicePackages();
        }catch(Throwable t){
            throw new RuntimeException("Cannot read service packages!", t);
        }

        ResourceConfig rc = new ResourceConfig().packages(packages);

        rc.register(DebugExceptionMapper.class);
        rc.register(ResponseResolver.class);

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        logger.info("Entered the main method...");
        final HttpServer server = startServer();


        logger.info(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));

        System.in.read();
        server.stop();
    }
}
