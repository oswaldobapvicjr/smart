package net.obvj.smart.agents.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeoutException;

import net.obvj.smart.conf.xml.XmlAgent;

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

    private String name;
    private String type;
    private int stopTimeoutSeconds = -1;
    
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

    protected void setStopTimeoutSeconds(int stopTimeoutSeconds)
    {
        this.stopTimeoutSeconds = stopTimeoutSeconds;
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
        return (startDate != null ? (Calendar) startDate.clone() : null);
    }

    /**
     * @return The date & time when this agent task was last executed (automatically or
     *         manually by the run method)
     */
    public Calendar getLastRunDate()
    {
        return (Calendar) lastRunDate.clone();
    }

    public abstract void start();

    public abstract void stop() throws TimeoutException;

    public int getStopTimeoutSeconds()
    {
        return stopTimeoutSeconds;
    }

    public abstract String getStatusString();

    /**
     * Creates a new Agent from the given XmlAgent
     * 
     * @throws ClassNotFoundException if the agent class cannot be found
     * @throws NoSuchMethodException  if the default agent constructor cannot be found
     * @throws IllegalAccessException if the agent constructor is not accessible
     * @throws InstantiationException if the agent cannot be instantiated
     * 
     * @since 2.0
     */
    public static Agent parseAgent(XmlAgent xmlAgent) throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, ClassNotFoundException
    {
        if (xmlAgent.getType().equals("timer"))
        {
            return TimerAgent.parseAgent(xmlAgent);
        }
        return DaemonAgent.parseAgent(xmlAgent);
    }

}
