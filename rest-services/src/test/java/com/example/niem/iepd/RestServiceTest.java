package com.example.niem.iepd;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.github.benas.randombeans.api.EnhancedRandom;
import org.glassfish.grizzly.http.server.HttpServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class RestServiceTest {
    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        server = Main.startServer();

        Client c = ClientBuilder.newClient();
        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void testGetExchange() {
        String responseMsg = target.path("template/exchange/12").request().get(String.class);
        assert responseMsg != null;
        assert responseMsg.contains("<template:TemplateExchange");
    }

    @Test
    public void testSendExchange() {
        Response response = target.path("template/exchange")
                .request(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
                .post(Entity.entity(new File("target/test-classes/sample-exchange.xml"),MediaType.APPLICATION_XML));
        assertEquals(response.getStatus(),(Response.Status.CREATED.getStatusCode()));
    }
}