package net.obvj.smart.util;

import static org.junit.Assert.*;

import org.junit.Test;

import net.obvj.smart.util.DateUtil.TimeUnit;

/**
 * Unit tests for the {@link TimeInterval} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class TimeIntervalTest
{
    private void assertTimeIntervalOf(String input, int expectedDuration, TimeUnit expectedTimeUnit)
    {
        TimeInterval timeInterval = TimeInterval.of(input);
        assertEquals(expectedDuration, timeInterval.getDuration());
        assertEquals(expectedTimeUnit, timeInterval.getTimeUnit());
    }

    @Test
    public void testExtractFirstDigitsFromValidString()
    {
        assertEquals(1, TimeInterval.extractFirstDigitGroupFromString("1minute"));
        assertEquals(5, TimeInterval.extractFirstDigitGroupFromString("5m"));
        assertEquals(10, TimeInterval.extractFirstDigitGroupFromString("10minutes"));
        assertEquals(15, TimeInterval.extractFirstDigitGroupFromString("15 minutes"));
        assertEquals(20, TimeInterval.extractFirstDigitGroupFromString("20 m"));
        assertEquals(25, TimeInterval.extractFirstDigitGroupFromString("25"));
        assertEquals(30, TimeInterval.extractFirstDigitGroupFromString(" 30 "));
        assertEquals(35, TimeInterval.extractFirstDigitGroupFromString("35.5s"));
        assertEquals(40, TimeInterval.extractFirstDigitGroupFromString("40,5s"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractFirstDigitsFromInvalidString()
    {
        assertEquals(1, TimeInterval.extractFirstDigitGroupFromString("minute"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractFirstDigitsFromEmptyString()
    {
        assertEquals(5, TimeInterval.extractFirstDigitGroupFromString(""));
    }

    @Test
    public void testExtractLetterFromValidString()
    {
        assertEquals("minute", TimeInterval.extractFirstLetterGroupFromString("1minute"));
        assertEquals("minutes", TimeInterval.extractFirstLetterGroupFromString("10 minutes"));
        assertEquals("m", TimeInterval.extractFirstLetterGroupFromString("5m"));
        assertEquals("H", TimeInterval.extractFirstLetterGroupFromString("15 H"));
        assertEquals("seconds", TimeInterval.extractFirstLetterGroupFromString("20 seconds "));
        assertEquals("", TimeInterval.extractFirstLetterGroupFromString("25"));
        assertEquals("", TimeInterval.extractFirstLetterGroupFromString(" 30 "));
        assertEquals("hours", TimeInterval.extractFirstLetterGroupFromString("35.5 hours"));
        assertEquals("s", TimeInterval.extractFirstLetterGroupFromString("40,5s"));
    }

    @Test
    public void testTimeIntervalOfValidStrings()
    {
        assertTimeIntervalOf("1minute", 1, TimeUnit.MINUTES);
        assertTimeIntervalOf("5m", 5, TimeUnit.MINUTES);
        assertTimeIntervalOf("10minutes", 10, TimeUnit.MINUTES);
        assertTimeIntervalOf("15 Minutes", 15, TimeUnit.MINUTES);
        assertTimeIntervalOf("20 m", 20, TimeUnit.MINUTES);
        assertTimeIntervalOf("25", 25, TimeUnit.MINUTES);
        assertTimeIntervalOf(" 30 ", 30, TimeUnit.MINUTES);
        assertTimeIntervalOf("35_MINUTE", 35, TimeUnit.MINUTES);
        assertTimeIntervalOf("1H", 1, TimeUnit.HOURS);
        assertTimeIntervalOf("2h", 2, TimeUnit.HOURS);
        assertTimeIntervalOf("12-HOURS", 12, TimeUnit.HOURS);
        assertTimeIntervalOf("hour=3", 3, TimeUnit.HOURS);
        assertTimeIntervalOf("seconds=15", 15, TimeUnit.SECONDS);
        assertTimeIntervalOf("SeCoNd:3", 3, TimeUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTimeIntervalOfUnknownTimeUnit()
    {
        TimeInterval.of("1byte");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testTimeIntervalOfEmptyString()
    {
        TimeInterval.of("");
    }

}
