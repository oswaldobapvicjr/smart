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
        SECONDS(Calendar.SECOND, "second(s)"),
        MINUTES(Calendar.MINUTE, "minute(s)"),
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

    public static Date getExactStartDateEveryMinute(int intervalInMinutes)
    {
        Calendar now = Calendar.getInstance();
        Calendar start = (Calendar) now.clone();

        int minute = now.get(Calendar.MINUTE);
        int minutesDiff = (minute % intervalInMinutes == 0) ? 0 : intervalInMinutes - minute % intervalInMinutes;

        start.add(Calendar.MINUTE, minutesDiff);

        if (start.before(now) || start.equals(now))
        {
            start.add(Calendar.MINUTE, intervalInMinutes);
        }
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        return start.getTime();
    }

    public static Date getExactStartDateEvery(int interval, TimeUnit timeUnit)
    {
        Calendar now = Calendar.getInstance();
        Calendar start = (Calendar) now.clone();

        int time = now.get(timeUnit.calendarConstant);
        int timeDiff = (time % interval == 0) ? 0 : interval - time % interval;

        start.add(timeUnit.calendarConstant, timeDiff);

        if (start.before(now) || start.equals(now))
        {
            start.add(timeUnit.calendarConstant, interval);
        }

        switch (timeUnit)
        {
        case HOURS:
            start.set(Calendar.MINUTE, 0);
            break;
        case MINUTES:
            start.set(Calendar.SECOND, 0);
            break;
        case SECONDS:
            start.set(Calendar.MILLISECOND, 0);
            break;
        }

        return start.getTime();
    }

}
