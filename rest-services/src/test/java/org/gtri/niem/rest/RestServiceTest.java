package org.gtri.niem.rest;


import org.apache.log4j.Logger;

import org.junit.*;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class RestServiceTest extends AbstractTest {
    private static Logger logger = Logger.getLogger(RestServiceTest.class);

    @Test
    public void testGetExchange() {
        logger.info("Testing a random exchange is generated...");
        String responseMsg = target.path("sample/random/12").request().get(String.class);
        assertThat(responseMsg, notNullValue());
        logger.debug("Response: "+responseMsg);

        logger.info("Successfully tested random exchange result!");
    }


}