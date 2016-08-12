package com.example.niem.iepd;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.benas.randombeans.api.EnhancedRandom;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.*;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class RestServiceTest {
    private static Logger logger = Logger.getLogger(RestServiceTest.class);

    private static HttpServer server;
    private static WebTarget target;


    @BeforeClass
    public static void setUp() throws Exception {
        server = Main.startServer();
        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stop();
    }

    @After
    public void printStart(){
        logger.info("======================================== STARTING TEST ========================================");
    }
    @Before
    public void printStop(){
        logger.info("======================================== STOPPING TEST ========================================\n\n");
    }


    @Test
    public void testGetExchange() {
        logger.info("Testing a random exchange is generated...");
        String responseMsg = target.path("sample/random/12").request().get(String.class);
        assert responseMsg != null;

        logger.debug("Response: "+responseMsg);

        logger.info("Successfully tested random exchange result!");
    }

    @Test
    public void testSendExchange() {
        logger.info("Testing that we can put a sample instance...");
        Response response = target.path("sample/pretty-print")
                .request(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
                .post(Entity.entity(new File("target/test-classes/sample-exchange.xml"),MediaType.APPLICATION_XML));
        assertEquals(response.getStatus(),(Response.Status.CREATED.getStatusCode()));
    }
}