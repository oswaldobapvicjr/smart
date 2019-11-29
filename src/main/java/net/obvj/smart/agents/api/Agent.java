package net.obvj.smart.agents.api;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

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

    private AgentConfiguration configuration;

    private String name;
    private String type;
    private int stopTimeoutSeconds = Integer.MAX_VALUE;

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

    public Agent()
    {
        threadFactory = new AgentThreadFactory(name);
        schedule = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    protected void setName(String name)
    {
        this.name = name;
        threadFactory.setAgentName(name);
    }

    /**
     * @return This agent's identifier name
     */
    public String getName()
    {
        return name;
    }

    protected void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return This agent's type
     */
    public String getType()
    {
        return type.toLowerCase();
    }

    /**
     * @param stopTimeoutSeconds the stop timeout in seconds to set.
     */
    protected void setStopTimeoutSeconds(int stopTimeoutSeconds)
    {
        this.stopTimeoutSeconds = stopTimeoutSeconds;
    }

    protected void setConfiguration(AgentConfiguration configuration)
    {
        this.configuration = configuration;
    }

    /**
     * @return This agent's source configuration data
     */
    public AgentConfiguration getConfiguration()
    {
        return configuration;
    }

    /**
     * @return Whether this agent is configured to start automatically, default is {@code true}. 
     */
    public boolean isAutomaticallyStarted()
    {
        return getConfiguration() == null || getConfiguration().isAutomaticallyStarted();
    }
    
    /**
     * @return Whether this agent is hidden, default is {@code false}. 
     */
    public boolean isHidden()
    {
        return getConfiguration() == null || getConfiguration().isHidden();
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
        return (getState() == State.STARTED);
    }

    /**
     * @return <code>true</code> if this agent's task is currently running or
     *         <code>false</code> instead.
     */
    public boolean isRunning()
    {
        return (getState() == State.RUNNING);
    }

    /**
     * @return <code>true</code> if this agent's timer (not its task) is currently started or
     *         <code>false</code> instead.
     */
    public boolean isStopped()
    {
        return (getState() == State.STOPPED);
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

    public int getStopTimeoutSeconds()
    {
        return stopTimeoutSeconds >= 0 ? stopTimeoutSeconds : Integer.MAX_VALUE;
    }

    public abstract String getStatusString();


    /**
     * Creates a new Agent from the given {@link AgentConfiguration}.
     * 
     * @throws ReflectiveOperationException if the agent class or constructor cannot be found,
     *                                      or the constructor is not accessible, or the agent
     *                                      cannot be instantiated
     * @throws NullPointerException         if a null agent configuration is received
     * @throws IllegalArgumentException     if an unknown agent type is received
     * @since 2.0
     */
    public static Agent parseAgent(AgentConfiguration configuration) throws ReflectiveOperationException
    {
        String lType = configuration.getType();
        if (lType.equalsIgnoreCase("timer"))
        {
            return TimerAgent.parseAgent(configuration);
        }
        else if (lType.equalsIgnoreCase("daemon"))
        {
            return DaemonAgent.parseAgent(configuration);
        }
        throw Exceptions.illegalArgument("Unknown agent type: \"%s\"", lType);
    }

}
