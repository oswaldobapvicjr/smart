package net.obvj.smart.agents;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

import net.obvj.smart.agents.impl.AnnotatedTimerAgent;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.util.DateUtils;
import net.obvj.smart.util.Exceptions;

/**
 * A common interface for all managed agents
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public abstract class Agent implements Runnable
{

    public enum State
    {
        SET, STARTED, RUNNING, STOPPED, ERROR;
    }

    private final AgentConfiguration configuration;

    private State currentState;

    private AgentThreadFactory threadFactory;
    protected ScheduledExecutorService schedule;

    /*
     * Stores the date & time this agent was started (schedule)
     */
    protected Calendar startDate;
    /*
     * Stores the date & time when this agent task was last executed
     */
    protected Calendar lastRunDate;

    public Agent(AgentConfiguration configuration)
    {
        this.configuration = configuration;
        threadFactory = new AgentThreadFactory(getName());
        schedule = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    /**
     * @return This agent's identifier name, as in {@link AgentConfiguration}.
     */
    public String getName()
    {
        return configuration.getName();
    }

    /**
     * @return This agent's type, as in {@link AgentConfiguration}.
     */
    public String getType()
    {
        return configuration.getType().toLowerCase();
    }

    /**
     * @return This agent's source configuration data
     */
    public AgentConfiguration getConfiguration()
    {
        return configuration;
    }

    /**
     * @return Whether this agent is configured to start automatically, as in
     *         {@link AgentConfiguration}.
     */
    public boolean isAutomaticallyStarted()
    {
        return configuration.isAutomaticallyStarted();
    }

    /**
     * @return Whether this agent is hidden, as in {@link AgentConfiguration}.
     */
    public boolean isHidden()
    {
        return getConfiguration().isHidden();
    }

    protected void setState(State currentState)
    {
        this.currentState = currentState;
    }

    /**
     * @return This agent's current state
     */
    public State getState()
    {
        return currentState;
    }

    /**
     * @return <code>true</code> if this agent's timer (not its task) is currently started or
     *         <code>false</code> instead.
     */
    public boolean isStarted()
    {
        return (currentState == State.STARTED);
    }

    /**
     * @return <code>true</code> if this agent's task is currently running or
     *         <code>false</code> instead.
     */
    public boolean isRunning()
    {
        return (currentState == State.RUNNING);
    }

    /**
     * @return <code>true</code> if this agent's timer (not its task) is currently started or
     *         <code>false</code> instead.
     */
    public boolean isStopped()
    {
        return (currentState == State.STOPPED);
    }

    /**
     * @return The date & time this agent was started (scheduled)
     */
    public Calendar getStartDate()
    {
        return DateUtils.getClonedDate(startDate);
    }

    /**
     * @return The date & time when this agent task was last executed (automatically or
     *         manually by the run method)
     */
    public Calendar getLastRunDate()
    {
        return DateUtils.getClonedDate(lastRunDate);
    }

    public abstract void start();

    public abstract void stop() throws TimeoutException;

    public abstract void run(boolean manualFlag);

    /**
     * @return This agent's stop timeout in seconds, as in {@link AgentConfiguration}. If a
     *         negative amount is configured, then {@link Integer#MAX_VALUE} will be returned.
     */
    public int getStopTimeoutSeconds()
    {
        int stopTimeoutSeconds = configuration.getStopTimeoutInSeconds();
        return stopTimeoutSeconds >= 0 ? stopTimeoutSeconds : Integer.MAX_VALUE;
    }

    public abstract String getStatusString();

    /**
     * Creates a new Agent from the given {@link AgentConfiguration}.
     *
     * @throws NullPointerException     if a null agent configuration is received
     * @throws IllegalArgumentException if an unknown agent type is received
     * @since 2.0
     */
    public static Agent parseAgent(AgentConfiguration configuration)
    {
        String lType = configuration.getType();
        if (lType.equalsIgnoreCase("timer"))
        {
            return new AnnotatedTimerAgent(configuration);
        }
        throw Exceptions.illegalArgument("Unknown agent type: \"%s\"", lType);
    }

}
