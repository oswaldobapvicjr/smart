package net.obvj.smart.conf.annotation;

/**
 * Available types for the {@link Agent} annotation.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public enum Type
{
    /**
     * A timer agent is an object that, once started, runs a particular task periodically,
     * given a configurable interval in seconds, minutes, or hours.
     */
    TIMER("timer"),

    /**
     * An agent that, once started, runs a particular task at specified times and dates,
     * similar to the Cron service available in Unix/Linux systems.
     */
    CRON("cron");

    private final String value;

    private Type(String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return value;
    }

}
