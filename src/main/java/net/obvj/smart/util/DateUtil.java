package net.obvj.smart.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Utility methods for working with dates
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class DateUtil
{
    /**
     * Supported time units for timer agents maintenance
     */
    public enum TimeUnit
    {
        SECONDS(java.util.concurrent.TimeUnit.SECONDS, Calendar.SECOND, Arrays.asList("second", "seconds", "s", "S"), "second(s)"),
        MINUTES(java.util.concurrent.TimeUnit.MINUTES, Calendar.MINUTE, Arrays.asList("minute", "minutes", "m", "M"), "minute(s)"),
        HOURS(java.util.concurrent.TimeUnit.HOURS, Calendar.HOUR_OF_DAY, Arrays.asList("hour", "hours", "h", "H"), "hour(s)");

        public static final TimeUnit DEFAULT = TimeUnit.MINUTES;

        private final java.util.concurrent.TimeUnit javaTimeUnit;
        private final int calendarConstant;
        private final List<String> identifiers;
        private final String displayText;

        private TimeUnit(java.util.concurrent.TimeUnit javaTimeUnit, int calendarField, List<String> aliases,
                String strText)
        {
            this.javaTimeUnit = javaTimeUnit;
            this.calendarConstant = calendarField;
            this.identifiers = aliases;
            this.displayText = strText;
        }

        /**
         * @param identifier a string that can be used to retrieve a TimeUnit
         * @return the TimeUnit identified by the given string
         * @throws IllegalArgumentException if no time unit matches the given identifier
         */
        public static TimeUnit findByIdentifier(String identifier)
        {
            return Arrays.stream(TimeUnit.values()).filter(timeUnit -> isTimeUnitIdentifiableBy(identifier, timeUnit))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Invalid time unit identifier: " + identifier));
        }

        private static boolean isTimeUnitIdentifiableBy(String identifier, TimeUnit timeUnit)
        {
            return timeUnit.identifiers.stream()
                    .anyMatch(timeUnitIdentifier -> timeUnitIdentifier.equalsIgnoreCase(identifier));
        }

        /**
         * @return The {@link Calendar} constant associated with this time unit
         */
        public int getCalendarConstant()
        {
            return calendarConstant;
        }

        @Override
        public String toString()
        {
            return displayText;
        }

        public long toMillis(long amount)
        {
            return javaTimeUnit.toMillis(amount);
        }
    }

    private static final ThreadLocal<DateFormat> SIMPLE_FORMAT = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    private DateUtil()
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
