package net.obvj.smart.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

/**
 * Unit tests for the {@link TimeUnit} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class TimeUnitTest
{
    /**
     * Test TimeUnit identification based on configured string identifiers
     */
    @Test
    public void testFindTimeUnitByKnownIdentifiers()
    {
        assertEquals(TimeUnit.SECONDS, TimeUnit.findByIdentifier("second"));
        assertEquals(TimeUnit.SECONDS, TimeUnit.findByIdentifier("SECOND"));
        assertEquals(TimeUnit.SECONDS, TimeUnit.findByIdentifier("seconds"));
        assertEquals(TimeUnit.SECONDS, TimeUnit.findByIdentifier("SECONDS"));
        assertEquals(TimeUnit.SECONDS, TimeUnit.findByIdentifier("s"));
        assertEquals(TimeUnit.SECONDS, TimeUnit.findByIdentifier("S"));

        assertEquals(TimeUnit.MINUTES, TimeUnit.findByIdentifier("minute"));
        assertEquals(TimeUnit.MINUTES, TimeUnit.findByIdentifier("MINUTE"));
        assertEquals(TimeUnit.MINUTES, TimeUnit.findByIdentifier("minutes"));
        assertEquals(TimeUnit.MINUTES, TimeUnit.findByIdentifier("MINUTES"));
        assertEquals(TimeUnit.MINUTES, TimeUnit.findByIdentifier("m"));
        assertEquals(TimeUnit.MINUTES, TimeUnit.findByIdentifier("M"));

        assertEquals(TimeUnit.HOURS, TimeUnit.findByIdentifier("hour"));
        assertEquals(TimeUnit.HOURS, TimeUnit.findByIdentifier("HOUR"));
        assertEquals(TimeUnit.HOURS, TimeUnit.findByIdentifier("hours"));
        assertEquals(TimeUnit.HOURS, TimeUnit.findByIdentifier("HOURS"));
        assertEquals(TimeUnit.HOURS, TimeUnit.findByIdentifier("h"));
        assertEquals(TimeUnit.HOURS, TimeUnit.findByIdentifier("H"));
    }

    /**
     * Test TimeUnit identification for an unknown string identifier
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindTimeUnitByUnknownIdentifier()
    {
        TimeUnit.findByIdentifier("x");
    }

    /**
     * Test TimeUnit identification for null identifier
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFindTimeUnitWithNullIdentifier()
    {
        TimeUnit.findByIdentifier(null);
    }

    /**
     * Test Calendar constant associations by TimeUnit
     */
    @Test
    public void testTimeUnitCalendarConstants()
    {
        assertEquals(Calendar.SECOND, TimeUnit.SECONDS.getCalendarConstant());
        assertEquals(Calendar.MINUTE, TimeUnit.MINUTES.getCalendarConstant());
        assertEquals(Calendar.HOUR_OF_DAY, TimeUnit.HOURS.getCalendarConstant());
    }

    /**
     * Test display strings returned by TimeUnit
     */
    @Test
    public void testTimeUnitDisplayStrings()
    {
        assertEquals("second(s)", TimeUnit.SECONDS.toString());
        assertEquals("minute(s)", TimeUnit.MINUTES.toString());
        assertEquals("hour(s)", TimeUnit.HOURS.toString());
    }

    /**
     * Test time unit conversion to milliseconds
     */
    @Test
    public void testTimeUnitToMilliseconds()
    {
        assertEquals(1000, TimeUnit.SECONDS.toMillis(1));
        assertEquals(60000, TimeUnit.MINUTES.toMillis(1));
        assertEquals(3600000, TimeUnit.HOURS.toMillis(1));
    }
}
