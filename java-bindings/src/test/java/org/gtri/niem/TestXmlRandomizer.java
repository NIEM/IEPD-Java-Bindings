package org.gtri.niem;

import io.github.benas.randombeans.api.EnhancedRandom;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.*;


/**
 * Created by brad on 8/19/16.
 */
@RunWith(Parameterized.class)
public class TestXmlRandomizer extends AbstractTest {

    private static final Logger logger = Logger.getLogger(TestJaxbGeneratedJava.class);

    @Parameterized.Parameters(name="TestXmlRandomizer: {0}")
    public static Collection<Object[]> data() {
        ArrayList<Object[]> data = new ArrayList<>();

        // TODO : Maybe we don't want this random, but rather fixed.
        Random random = new Random(System.currentTimeMillis());
        for( int i = 0; i < 100; i++ ){
            data.add(new Object[]{random.nextLong()});
        }

        return data;
    }

    private Long seed = -1l;
    public TestXmlRandomizer(Long seed){
        this.seed = seed;
    }


    @Test
    public void testRandomization() {
        logger.debug("Testing random object generation (seed="+seed+")...");

        // TODO In theory, giving the same id should generate the same result, not different ones.
        List<String> potentialClasses = JaxbHelper.getNonNIEMClassNames();
        if( potentialClasses == null || potentialClasses.size() == 0 ){
            Assert.fail("There are no classes which to generate randomly.");
        }
        Random random = new Random(System.currentTimeMillis());
        String className = potentialClasses.get(random.nextInt(potentialClasses.size()));
        logger.info("  Creating sample Class: "+className);
        try {
            Class clz = Class.forName(className);
            // TODO Figure out how to incorporate the seed here.
            Object instance = EnhancedRandom.random(clz);
            assertThat(instance, notNullValue());

            // TODO Any other?

        }catch(Throwable t){
            logger.error("An unexpected error occurred.", t);
            Assert.fail("Unexpected error using randomizer: "+t.toString());
        }

    }//test Randomizatn()

}
