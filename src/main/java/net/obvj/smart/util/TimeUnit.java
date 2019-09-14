package net.obvj.smart.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Supported time units for timer agents maintenance.
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public enum TimeUnit
{
    SECONDS(java.util.concurrent.TimeUnit.SECONDS, Calendar.SECOND, Arrays.asList("second", "seconds", "s", "S"),
            "second(s)"),
    MINUTES(java.util.concurrent.TimeUnit.MINUTES, Calendar.MINUTE, Arrays.asList("minute", "minutes", "m", "M"),
            "minute(s)"),
    HOURS(java.util.concurrent.TimeUnit.HOURS, Calendar.HOUR_OF_DAY, Arrays.asList("hour", "hours", "h", "H"),
            "hour(s)");

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

    /**
     * Converts the given time duration to milliseconds.
     * 
     * @param amount the time duration to be converted
     * @return the converted amount
     * @since 2.0
     */
    public long toMillis(long amount)
    {
        return javaTimeUnit.toMillis(amount);
    }

}
