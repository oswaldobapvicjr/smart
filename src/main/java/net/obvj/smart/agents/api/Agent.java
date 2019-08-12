package net.obvj.smart.agents.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
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

    protected State currentState;
    protected String name;
    protected String type;
    protected int stopTimeoutSeconds = -1;
    protected ThreadFactory threadFactory;
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
        threadFactory = new AgentThreadFactory();
        schedule = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    /**
     * @return This agent's identifier name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * @return This agent's type
     */
    public String getType()
    {
        return type.toLowerCase();
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
        return (startDate != null);
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

    class AgentThreadFactory implements ThreadFactory
    {
        static final String AGENT_NAME_PREFIX = "Agent-";

        public Thread newThread(Runnable runnable)
        {
            String threadName = AGENT_NAME_PREFIX + getName();
            Thread thread = new Thread(runnable, threadName);
            thread.setPriority(Thread.NORM_PRIORITY);
            if (thread.isDaemon())
            {
                thread.setDaemon(false);
            }
            return thread;
        }
    }
}
