package org.gtri.xmlRandomizer;

import io.github.benas.randombeans.api.EnhancedRandomParameters;
import io.github.benas.randombeans.api.Randomizer;
import io.github.benas.randombeans.api.RandomizerRegistry;

import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;


public class XmlRandomizerRegistry implements RandomizerRegistry {

    private final Map<Class<?>, Randomizer<?>> randomizers = new HashMap<>();

    @Override
    public void init(EnhancedRandomParameters enhancedRandomParameters) {
        long seed = enhancedRandomParameters.getSeed();
        randomizers.put(XMLGregorianCalendar.class,new XMLGregoriaCalendarRandomizer(seed));
    }

    @Override
    public Randomizer<?> getRandomizer(Field field) {
        return getRandomizer(field.getType());
    }

    @Override
    public Randomizer<?> getRandomizer(Class<?> aClass) {
        return randomizers.get(aClass);
    }
}
