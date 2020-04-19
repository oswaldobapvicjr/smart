package net.obvj.smart.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An object that represents a time interval.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class TimeInterval
{
    private static final Pattern DIGITS_GROUP_PATTERN = Pattern.compile("\\d+");
    private static final Pattern LETTERS_GROUP_PATTERN = Pattern.compile("[a-zA-Z]+");

    private final int duration;
    private final TimeUnit timeUnit;

    public TimeInterval(int duration, TimeUnit timeUnit)
    {
        this.duration = duration;
        this.timeUnit = timeUnit;
    }

    public static TimeInterval of(String input)
    {
        int digits = extractFirstDigitGroupFromString(input);
        String timeUnitDescription = extractFirstLetterGroupFromString(input);

        TimeUnit timeUnit = timeUnitDescription.isEmpty() ? TimeUnit.DEFAULT
                : TimeUnit.findByIdentifier(timeUnitDescription);

        return new TimeInterval(digits, timeUnit);
    }

    /**
     * Extracts the first group of digits found in the given string.
     * <p>
     * For example: the following call {@code extractFirstDigitGroupFromString("15s")} returns
     * {@code "15"}.
     *
     * @param input the source string
     * @return the first group of digits found in the input string, as integer
     */
    protected static int extractFirstDigitGroupFromString(String input)
    {
        Matcher matcher = DIGITS_GROUP_PATTERN.matcher(input);
        if (matcher.find())
        {
            return Integer.parseInt(matcher.group(0));
        }
        throw Exceptions.illegalArgument("No digit found in input string: \"%s\"", input);
    }

    /**
     * Extracts the first group of letters found in the given string.
     * <p>
     * For example: the following call {@code extractFirstLetterGroupFromString("1 minute")}
     * returns {@code "minute"}.
     *
     * @param input the source string
     * @return the first group of letters found in the input string
     */
    protected static String extractFirstLetterGroupFromString(String input)
    {
        Matcher matcher = LETTERS_GROUP_PATTERN.matcher(input);
        if (matcher.find())
        {
            return matcher.group(0);
        }
        return "";
    }

    /**
     * Returns the duration of this {@link TimeUnit}
     *
     * @return the duration
     */
    public int getDuration()
    {
        return duration;
    }

    /**
     * Returns the {@link TimeUnit} associated with this {@link TimeInterval}.
     *
     * @return the interval's time unit
     */
    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    /**
     * Returns this time interval's duration, in milliseconds.
     *
     * @return the interval duratioin, in milliseconds
     */
    public long toMillis()
    {
        return timeUnit.toMillis(duration);
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(duration).append(" ").append(timeUnit).toString();
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(duration, timeUnit);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof TimeInterval)) return false;
        TimeInterval other = (TimeInterval) obj;
        return duration == other.duration && timeUnit == other.timeUnit;
    }

}
