package net.obvj.smart.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import net.obvj.junit.utils.TestUtils;

/**
 * Unit tests for the {@link DateUtils} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class DateUtilTest
{
    static
    {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    private static Date toDate(int year, int month, int day, int hour, int minute, int second, int millisecond)
    {
        return toCalendar(year, month, day, hour, minute, second, millisecond).getTime();
    }

    private static Calendar toCalendar(int year, int month, int day, int hour, int minute, int second, int millisecond)
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, millisecond);
        return calendar;
    }

    /**
     * Tests that no instances of this utility class are created
     *
     * @throws Exception in case of error getting constructor metadata or instantiating the
     *                   private constructor via Reflection
     */
    @Test
    public void testNoInstancesAllowed() throws Exception
    {
        TestUtils.assertNoInstancesAllowed(DateUtils.class, IllegalStateException.class, "Utility class");
    }

    /**
     * Test successful date formatting to common string format
     */
    @Test
    public void testFormatDateWithValidDate()
    {
        Date date = toDate(2019, 6, 12, 18, 15, 1, 123);
        assertThat(DateUtils.formatDate(date), is("2019-06-12 18:15:01"));
    }

    /**
     * Test date formatting with null date
     */
    @Test
    public void testFormatDateWithNullDate()
    {
        Date date = null;
        assertThat(DateUtils.formatDate(date), is("null"));
    }

    /**
     * Test successful calendar formatting to common string format
     */
    @Test
    public void testFormatCalendarWithValidDate()
    {
        Calendar calendar = toCalendar(2019, 6, 12, 18, 15, 1, 123);
        assertThat(DateUtils.formatDate(calendar), is("2019-06-12 18:15:01"));
    }

    /**
     * Test calendar formatting with null date
     */
    @Test
    public void testFormatCalendarWithNullCalendar()
    {
        Calendar calendar = null;
        assertThat(DateUtils.formatDate(calendar), is("null"));
    }

    /**
     * Test successful exact start date calculation every 1 minute with the
     * {@code getExactStartDateEveryMinute(int, Calendar)} method and a given base date.
     * <p>
     * The resulting date must be:
     * <li>minute = the next minute</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     */
    @Test
    public void testGetExactStartDateEveryMinute1Minute()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 18, 15, 1, 123);
        Date exactStartDate = DateUtils.getExactStartDateEveryMinute(1, baseDate);
        assertThat(exactStartDate, is(toDate(2019, 6, 12, 18, 16, 0, 0)));
    }

    /**
     * Test successful exact start date calculation every 5 minutes with the
     * {@code getExactStartDateEveryMinute(int, Calendar)} method and a given base date.
     * <p>
     * The resulting date must be:
     * <li>minute = the next multiple of 5</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     */
    @Test
    public void testGetExactStartDateEveryMinute5Minutes()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 18, 16, 1, 123);
        Date exactStartDate = DateUtils.getExactStartDateEveryMinute(5, baseDate);
        assertThat(exactStartDate, is(toDate(2019, 6, 12, 18, 20, 0, 0)));
    }

    /**
     * Test successful exact start date calculation every 1 minute with the
     * {@code getExactStartDateEvery(int, TimeUnit, Calendar)} method and a given base date.
     * <p>
     * The resulting date must be:
     * <li>minute = the next minute</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     */
    @Test
    public void testGetExactStartDateEveryTimeUnit1Minute()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 18, 15, 1, 123);
        Date exactStartDate = DateUtils.getExactStartDateEvery(1, TimeUnit.MINUTES, baseDate);
        assertThat(exactStartDate, is(toDate(2019, 6, 12, 18, 16, 0, 0)));
    }

    /**
     * Test successful exact start date calculation every 5 minutes with the
     * {@code getExactStartDateEvery(int, TimeUnit, Calendar)} method and a given base date.
     * <p>
     * The resulting date must be:
     * <li>minute = the next multiple of 5</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     */
    @Test
    public void testGetExactStartDateEveryTimeUnit5Minutes()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 18, 16, 1, 123);
        Date exactStartDate = DateUtils.getExactStartDateEvery(5, TimeUnit.MINUTES, baseDate);
        assertThat(exactStartDate, is(toDate(2019, 6, 12, 18, 20, 0, 0)));
    }

    /**
     * Test successful exact start date calculation every 30 minutes with the
     * {@code getExactStartDateEvery(int, TimeUnit, Calendar)} method and a given base date.
     * <p>
     * The resulting date must be:
     * <li>minute = the next multiple of 30</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     */
    @Test
    public void testGetExactStartDateEveryTimeUnit30Minutes()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 18, 45, 1, 123);
        Date exactStartDate = DateUtils.getExactStartDateEvery(30, TimeUnit.MINUTES, baseDate);
        assertThat(exactStartDate, (is(toDate(2019, 6, 12, 19, 0, 0, 0))));
    }

    /**
     * Test successful exact start date calculation every 1 hour with the
     * {@code getExactStartDateEvery(int, TimeUnit, Calendar)} method and a given base date.
     * <p>
     * The resulting date must be:
     * <li>hour = the next hour</li>
     * <li>minute = 0</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     */
    @Test
    public void testGetExactStartDateEveryTimeUnit1Hour()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 23, 38, 1, 123);
        Date exactStartDate = DateUtils.getExactStartDateEvery(1, TimeUnit.HOURS, baseDate);
        assertThat(exactStartDate, is(toDate(2019, 6, 13, 0, 0, 0, 0)));
    }

    /**
     * Test successful exact start date calculation every 2 hours with the
     * {@code getExactStartDateEvery(int, TimeUnit, Calendar)} method and a given base date.
     * <p>
     * The resulting date must be:
     * <li>hour = the next multiple of 2</li>
     * <li>minute = 0</li>
     * <li>second = 0</li>
     * <li>millisecond = 0</li>
     */
    @Test
    public void testGetExactStartDateEveryTimeUnit2Hours()
    {
        Calendar baseDate = toCalendar(2019, 6, 12, 17, 16, 1, 123);
        Date exactStartDate = DateUtils.getExactStartDateEvery(2, TimeUnit.HOURS, baseDate);
        assertThat(exactStartDate, is(toDate(2019, 6, 12, 18, 0, 0, 0)));
    }

}
