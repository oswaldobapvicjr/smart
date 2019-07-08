package net.obvj.smart.agents.api;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeoutException;

/**
 * A common interface for all managed agents.
 */
public abstract class Agent implements Runnable
{

    public enum State
    {
        SET, STARTED, RUNNING, STOPPED, ERROR;
    };

    protected State currentState;
    protected String name;
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

    public abstract int getStopTimeoutSeconds();

    public abstract String getStatusString();

    class AgentThreadFactory implements ThreadFactory
    {
        static final String namePrefix = "Agent-";
        final ThreadGroup group;

        AgentThreadFactory()
        {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        public Thread newThread(Runnable runnable)
        {
            String name = namePrefix;
            name += getName();
            Thread thread = new Thread(group, runnable, name);
            thread.setPriority(Thread.NORM_PRIORITY);
            if (thread.isDaemon())
            {
                thread.setDaemon(false);
            }
            return thread;
        }
    }
}
