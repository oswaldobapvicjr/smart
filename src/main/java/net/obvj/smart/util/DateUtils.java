package net.obvj.smart.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility methods for working with dates
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class DateUtils
{
    private static final ThreadLocal<DateFormat> SIMPLE_FORMAT = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    private DateUtils()
    {
        throw new IllegalStateException("Utility class");
    }
    
    /**
     * @return current date and time, formatted
     */
    public static String now()
    {
        return formatDate(new Date());
    }

    /**
     * Formats the given calendar using internally standardized date format
     * 
     * @param calendar the calendar to be formatted
     * @return a formatted date
     */
    public static String formatDate(Calendar calendar)
    {
        return calendar != null ? formatDate(calendar.getTime()) : "null";
    }
    
    /**
     * Formats the given date using internally standardized date format
     * 
     * @param date the date to be formatted
     * @return a formatted date
     */
    public static String formatDate(Date date)
    {
        return date != null ? SIMPLE_FORMAT.get().format(date) : "null";
    }

    /**
     * Return the first start date for a given interval in minutes.
     * <p>
     * For example: if current time is 23:38:26 (MM:SS:mi), and the interval is set to 1, the
     * adjusted date will be 23:39:00.
     * 
     * @param intervalInMinutes the interval in minutes for the first start date
     * @return the adjusted start date for the given interval in minutes
     */
    public static Date getExactStartDateEveryMinute(int intervalInMinutes)
    {
        return getExactStartDateEveryMinute(intervalInMinutes, Calendar.getInstance());
    }

    protected static Date getExactStartDateEveryMinute(int intervalInMinutes, Calendar baseCalendar)
    {
        return getExactStartDateEvery(intervalInMinutes, TimeUnit.MINUTES, baseCalendar);
    }

    /**
     * Return the first start date for a given interval and time unit.
     * <p>
     * For example:
     * <ul>
     * <li>if current time is 23:38:26 (MM:SS:mi), and the interval is set to 1 with
     * {@code TimeUnit.MINUTES}, the adjusted date will be 23:39:00</li>
     * 
     * <li>if current time is 23:38:26 (MM:SS:mi), and the interval is set to 1 with
     * {@code TimeUnit.HOURS}, the adjusted date will be 00:00:00 (next day)</li>
     * </ul>
     * 
     * @param interval the interval for the first start date
     * @param timeUnit the given interval's time unit
     * @return the adjusted start date for the given interval and time unit
     */
    public static Date getExactStartDateEvery(int interval, TimeUnit timeUnit)
    {
        return getExactStartDateEvery(interval, timeUnit, Calendar.getInstance());
    }

    protected static Date getExactStartDateEvery(int interval, TimeUnit timeUnit, Calendar baseCalendar)
    {
        Calendar start = (Calendar) baseCalendar.clone();

        int time = baseCalendar.get(timeUnit.getCalendarConstant());
        int timeDiff = (time % interval == 0) ? 0 : interval - time % interval;

        start.add(timeUnit.getCalendarConstant(), timeDiff);

        if (start.before(baseCalendar) || start.equals(baseCalendar))
        {
            start.add(timeUnit.getCalendarConstant(), interval);
        }

        switch (timeUnit)
        {
        // NOTE: It is safe to ignore SonarQube squid:S128 here
        // We really want to continue executing the statements of the subsequent cases on purpose
        case HOURS:
            start.set(Calendar.MINUTE, 0);
        case MINUTES:
            start.set(Calendar.SECOND, 0);
        case SECONDS:
            start.set(Calendar.MILLISECOND, 0);
        }

        return start.getTime();
    }

    /**
     * Creates and returns a copy of the given calendar object.
     */
    public static Calendar getClonedDate(Calendar calendar)
    {
        return calendar != null ? (Calendar) calendar.clone() : null;
    }

}
