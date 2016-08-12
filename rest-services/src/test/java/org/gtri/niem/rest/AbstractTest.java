package org.gtri.niem.rest;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/**
 * Created by brad on 8/12/16.
 */
public abstract class AbstractTest {

    private static Logger logger = Logger.getLogger(AbstractTest.class);

    @Before
    public void printStart(){
        logger.info("======================================== STARTING TEST ========================================");
    }
    @After
    public void printStop(){
        logger.info("======================================== STOPPING TEST ========================================\n\n");
    }

    protected static HttpServer server;
    protected static WebTarget target;


    @BeforeClass
    public static void setUp() throws Exception {
        logger.info("STARTING SERVER FOR TESTS...");
        server = Main.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stop();
        logger.info("STOPPING SERVER!");
    }



}
