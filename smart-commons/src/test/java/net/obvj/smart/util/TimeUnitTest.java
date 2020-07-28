package net.obvj.smart.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        assertThat(TimeUnit.findByIdentifier("second"), is(TimeUnit.SECONDS));
        assertThat(TimeUnit.findByIdentifier("SECOND"), is(TimeUnit.SECONDS));
        assertThat(TimeUnit.findByIdentifier("seconds"), is(TimeUnit.SECONDS));
        assertThat(TimeUnit.findByIdentifier("SECONDS"), is(TimeUnit.SECONDS));
        assertThat(TimeUnit.findByIdentifier("s"), is(TimeUnit.SECONDS));
        assertThat(TimeUnit.findByIdentifier("S"), is(TimeUnit.SECONDS));

        assertThat(TimeUnit.findByIdentifier("minute"), is(TimeUnit.MINUTES));
        assertThat(TimeUnit.findByIdentifier("MINUTE"), is(TimeUnit.MINUTES));
        assertThat(TimeUnit.findByIdentifier("minutes"), is(TimeUnit.MINUTES));
        assertThat(TimeUnit.findByIdentifier("MINUTES"), is(TimeUnit.MINUTES));
        assertThat(TimeUnit.findByIdentifier("m"), is(TimeUnit.MINUTES));
        assertThat(TimeUnit.findByIdentifier("M"), is(TimeUnit.MINUTES));

        assertThat(TimeUnit.findByIdentifier("hour"), is(TimeUnit.HOURS));
        assertThat(TimeUnit.findByIdentifier("HOUR"), is(TimeUnit.HOURS));
        assertThat(TimeUnit.findByIdentifier("hours"), is(TimeUnit.HOURS));
        assertThat(TimeUnit.findByIdentifier("HOURS"), is(TimeUnit.HOURS));
        assertThat(TimeUnit.findByIdentifier("h"), is(TimeUnit.HOURS));
        assertThat(TimeUnit.findByIdentifier("H"), is(TimeUnit.HOURS));
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
        assertThat(TimeUnit.SECONDS.getCalendarConstant(), is(Calendar.SECOND));
        assertThat(TimeUnit.MINUTES.getCalendarConstant(), is(Calendar.MINUTE));
        assertThat(TimeUnit.HOURS.getCalendarConstant(), is(Calendar.HOUR_OF_DAY));
    }

    /**
     * Test display strings returned by TimeUnit
     */
    @Test
    public void testTimeUnitDisplayStrings()
    {
        assertThat(TimeUnit.SECONDS.toString(), is("second(s)"));
        assertThat(TimeUnit.MINUTES.toString(), is("minute(s)"));
        assertThat(TimeUnit.HOURS.toString(), is("hour(s)"));
    }

    /**
     * Test time unit conversion to milliseconds
     */
    @Test
    public void testTimeUnitToMilliseconds()
    {
        assertThat(TimeUnit.SECONDS.toMillis(1), is(1000l));
        assertThat(TimeUnit.MINUTES.toMillis(1), is(60000l));
        assertThat(TimeUnit.HOURS.toMillis(1), is(3600000l));
    }

    /**
     * Test time unit conversion, based on another source time unit
     */
    @Test
    public void testTimeUnitConvert()
    {
        assertThat(TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES), is(60l));
        assertThat(TimeUnit.SECONDS.convert(1, TimeUnit.HOURS), is(3600l));
        assertThat(TimeUnit.MINUTES.convert(1, TimeUnit.HOURS), is(60l));
    }
}
