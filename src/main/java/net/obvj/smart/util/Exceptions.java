package net.obvj.smart.util;

import java.io.FileNotFoundException;

/**
 * Shorthands creating exceptions with a formatted message.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public final class Exceptions
{
    private Exceptions()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Creates an {@link IllegalArgumentException} with a formatted message.
     * 
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return an {@link IllegalArgumentException} with a formatted message
     */
    public static IllegalArgumentException illegalArgument(final String format, final Object... args)
    {
        return new IllegalArgumentException(String.format(format, args));
    }

    /**
     * Creates an {@link IllegalArgumentException} with a formatted message.
     * 
     * @param throwable the cause to be set
     * @param format    See {@link String#format(String, Object...)}
     * @param args      See {@link String#format(String, Object...)}
     * @return an {@link IllegalArgumentException} with a formatted message
     */
    public static IllegalArgumentException illegalArgument(final Throwable throwable, final String format,
            final Object... args)
    {
        return new IllegalArgumentException(String.format(format, args), throwable);
    }

    /**
     * Creates an {@link IllegalStateException} with a formatted message.
     * 
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return an {@link IllegalStateException} with a formatted message
     */
    public static IllegalStateException illegalState(final String format, final Object... args)
    {
        return new IllegalStateException(String.format(format, args));
    }

    /**
     * Creates an {@link IllegalStateException} with a formatted message.
     * 
     * @param throwable the cause to be set
     * @param format    See {@link String#format(String, Object...)}
     * @param args      See {@link String#format(String, Object...)}
     * @return an {@link IllegalStateException} with a formatted message
     */
    public static IllegalStateException illegalState(final Throwable throwable, final String format,
            final Object... args)
    {
        return new IllegalStateException(String.format(format, args), throwable);
    }

    /**
     * Creates a {@link FileNotFoundException} with a formatted message.
     * 
     * @param format See {@link String#format(String, Object...)}
     * @param args   See {@link String#format(String, Object...)}
     * @return a {@link FileNotFoundException} with a formatted message
     */
    public static FileNotFoundException fileNotFound(final String format, final Object... args)
    {
        return new FileNotFoundException(String.format(format, args));
    }

}
