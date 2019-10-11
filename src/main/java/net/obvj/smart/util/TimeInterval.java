package net.obvj.smart.util;

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

    public int getDuration()
    {
        return duration;
    }

    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }
}
