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
public class DateUtil
{
    /**
     * Supporter time units for timer agents maintenance
     */
    public enum TimeUnit
    {
        SECONDS(Calendar.SECOND, "second(s)"), MINUTES(Calendar.MINUTE, "minute(s)"),
        HOURS(Calendar.HOUR_OF_DAY, "hour(s)");

        private int calendarConstant;
        private String strText;

        private TimeUnit(int calendarField, String strText)
        {
            this.calendarConstant = calendarField;
            this.strText = strText;
        }

        @Override
        public String toString()
        {
            return strText;
        }
    }

    private static final ThreadLocal<DateFormat> SIMPLE_FORMAT = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    /**
     * @return current date and time, formatted
     */
    public static String now()
    {
        return formatDate(new Date());
    }

    /**
     * Formats the given date using internally standardized date format
     * 
     * @param date the date to be formatted
     * @return a formatted date
     */
    public static String formatDate(Date date)
    {
        return SIMPLE_FORMAT.get().format(date);
    }

    /**
     * Return the first start date for a given interval in minutes.
     * <p>
     * For example: if current time is 23:38:26 (MM:SS:mi), the adjusted date to be retrieved
     * will be 23:39:00
     * 
     * @param intervalInMinutes the interval in minutes for the first start date
     * @return the adjusted start date for the given interval in minutes.
     */
    public static Date getExactStartDateEveryMinute(int intervalInMinutes)
    {
        return getExactStartDateEveryMinute(intervalInMinutes, Calendar.getInstance());
    }
    
    public static Date getExactStartDateEveryMinute(int intervalInMinutes, Calendar baseCalendar)
    {
        return getExactStartDateEvery(intervalInMinutes, TimeUnit.MINUTES, baseCalendar);
    }

    public static Date getExactStartDateEvery(int interval, TimeUnit timeUnit)
    {
        return getExactStartDateEvery(interval, timeUnit, Calendar.getInstance());
    }

    protected static Date getExactStartDateEvery(int interval, TimeUnit timeUnit, Calendar baseCalendar)
    {
        Calendar start = (Calendar) baseCalendar.clone();

        int time = baseCalendar.get(timeUnit.calendarConstant);
        int timeDiff = (time % interval == 0) ? 0 : interval - time % interval;

        start.add(timeUnit.calendarConstant, timeDiff);

        if (start.before(baseCalendar) || start.equals(baseCalendar))
        {
            start.add(timeUnit.calendarConstant, interval);
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

}
