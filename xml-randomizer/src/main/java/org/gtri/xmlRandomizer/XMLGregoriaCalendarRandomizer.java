package org.gtri.xmlRandomizer;

import io.github.benas.randombeans.api.Randomizer;
import io.github.benas.randombeans.randomizers.time.GregorianCalendarRandomizer;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Randomizer for XMlGregorianCalendar that uses GregorianCalendarRandomizer
 */
public class XMLGregoriaCalendarRandomizer implements Randomizer<XMLGregorianCalendar> {

    private final GregorianCalendarRandomizer gregorianCalendarRandomizer;

    public XMLGregoriaCalendarRandomizer(long seed) {
        gregorianCalendarRandomizer = new GregorianCalendarRandomizer(seed);
    }

    @Override
    public XMLGregorianCalendar getRandomValue() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendarRandomizer.getRandomValue());
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
