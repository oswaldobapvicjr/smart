package net.obvj.smart.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.util.CollectionUtils;

/**
 * Unit tests for the {@link TimeInterval} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class TimeIntervalTest
{
    private void assertTimeIntervalOf(int expectedDuration, TimeUnit expectedTimeUnit, String input)
    {
        TimeInterval timeInterval = TimeInterval.of(input);
        assertThat(timeInterval.getDuration(), is(expectedDuration));
        assertThat(timeInterval.getTimeUnit(), is(expectedTimeUnit));
    }

    @Test
    public void testExtractFirstDigitsFromValidString()
    {
        assertThat(TimeInterval.extractFirstDigitGroupFromString("1minute"), is(1));
        assertThat(TimeInterval.extractFirstDigitGroupFromString("5m"), is(5));
        assertThat(TimeInterval.extractFirstDigitGroupFromString("10minutes"), is(10));
        assertThat(TimeInterval.extractFirstDigitGroupFromString("15 minutes"), is(15));
        assertThat(TimeInterval.extractFirstDigitGroupFromString("20 m"), is(20));
        assertThat(TimeInterval.extractFirstDigitGroupFromString("25"), is(25));
        assertThat(TimeInterval.extractFirstDigitGroupFromString(" 30 "), is(30));
        assertThat(TimeInterval.extractFirstDigitGroupFromString("35.5s"), is(35));
        assertThat(TimeInterval.extractFirstDigitGroupFromString("40,5s"), is(40));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractFirstDigitsFromInvalidString()
    {
        TimeInterval.extractFirstDigitGroupFromString("minute");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractFirstDigitsFromEmptyString()
    {
        TimeInterval.extractFirstDigitGroupFromString("");
    }

    @Test
    public void testExtractLetterFromValidString()
    {
        assertThat(TimeInterval.extractFirstLetterGroupFromString("1minute"), is("minute"));
        assertThat(TimeInterval.extractFirstLetterGroupFromString("10 minutes"), is("minutes"));
        assertThat(TimeInterval.extractFirstLetterGroupFromString("5m"), is("m"));
        assertThat(TimeInterval.extractFirstLetterGroupFromString("15 H"), is("H"));
        assertThat(TimeInterval.extractFirstLetterGroupFromString("20 seconds "), is("seconds"));
        assertThat(TimeInterval.extractFirstLetterGroupFromString("25"), is(""));
        assertThat(TimeInterval.extractFirstLetterGroupFromString(" 30 "), is(""));
        assertThat(TimeInterval.extractFirstLetterGroupFromString("35.5 hours"), is("hours"));
        assertThat(TimeInterval.extractFirstLetterGroupFromString("40,5s"), is("s"));
    }

    @Test
    public void testTimeIntervalOfValidStrings()
    {
        assertTimeIntervalOf(1, TimeUnit.MINUTES, "1minute");
        assertTimeIntervalOf(5, TimeUnit.MINUTES, "5m");
        assertTimeIntervalOf(10, TimeUnit.MINUTES, "10minutes");
        assertTimeIntervalOf(15, TimeUnit.MINUTES, "15 Minutes");
        assertTimeIntervalOf(20, TimeUnit.MINUTES, "20 m");
        assertTimeIntervalOf(25, TimeUnit.MINUTES, "25");
        assertTimeIntervalOf(30, TimeUnit.MINUTES, " 30 ");
        assertTimeIntervalOf(35, TimeUnit.MINUTES, "35_MINUTE");
        assertTimeIntervalOf(1, TimeUnit.HOURS, "1H");
        assertTimeIntervalOf(2, TimeUnit.HOURS, "2h");
        assertTimeIntervalOf(12, TimeUnit.HOURS, "12-HOURS");
        assertTimeIntervalOf(3, TimeUnit.HOURS, "hour=3");
        assertTimeIntervalOf(15, TimeUnit.SECONDS, "seconds=15");
        assertTimeIntervalOf(3, TimeUnit.SECONDS, "SeCoNd:3");
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

    @Test
    public void testEqualsAndHashCode()
    {
        Set<TimeInterval> set = new HashSet<>();
        set.add(TimeInterval.of("1 second"));
        set.add(TimeInterval.of("1s"));
        set.add(TimeInterval.of("1SECOND"));
        assertThat(set.size(), is(1));
    }

    @Test
    public void testEquals()
    {
        assertThat(TimeInterval.of("10 seconds").equals(TimeInterval.of("10 s")), is(true));
        assertThat(TimeInterval.of("10 seconds").equals(null), is(false));
        assertThat(TimeInterval.of("10 seconds").equals(new Object()), is(false));
    }

    @Test
    public void testToMillis()
    {
        assertThat(TimeInterval.of("2seconds").toMillis(), is(2000L));
    }

}
