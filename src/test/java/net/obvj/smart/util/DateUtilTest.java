package net.obvj.smart.util;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import net.obvj.smart.util.DateUtil.TimeUnit;

/**
 * Unit tests for the {@link DateUtil} class.
 * 
 * @author oswaldo.bapvic.jr
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
     * Test successful date formatting to common string format
     */
    @Test
    public void testFormatDateWithValidDate()
    {
        Date date = toDate(2019, 6, 12, 18, 15, 1, 123);
        assertThat(DateUtil.formatDate(date), is("2019-06-12 18:15:01"));
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
        Date exactStartDate = DateUtil.getExactStartDateEveryMinute(1, baseDate);
        assertEquals(toDate(2019, 6, 12, 18, 16, 0, 0), exactStartDate);
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
        Date exactStartDate = DateUtil.getExactStartDateEveryMinute(5, baseDate);
        assertEquals(toDate(2019, 6, 12, 18, 20, 0, 0), exactStartDate);
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
        Date exactStartDate = DateUtil.getExactStartDateEvery(1, TimeUnit.MINUTES, baseDate);
        assertEquals(toDate(2019, 6, 12, 18, 16, 0, 0), exactStartDate);
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
        Date exactStartDate = DateUtil.getExactStartDateEvery(5, TimeUnit.MINUTES, baseDate);
        assertEquals(toDate(2019, 6, 12, 18, 20, 0, 0), exactStartDate);
    }

}
