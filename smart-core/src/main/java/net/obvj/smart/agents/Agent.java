package net.obvj.smart.agents;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.EvictingQueue;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Stopwatch;
import net.obvj.performetrics.util.Duration;
import net.obvj.performetrics.util.Duration.FormatStyle;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.util.DateUtils;
import net.obvj.smart.util.StatisticsUtils;

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

    private static final Logger LOG = LoggerFactory.getLogger(Agent.class);

    protected static final String MSG_AGENT_ALREADY_STARTED = "Agent already started";
    protected static final String MSG_AGENT_ALREADY_STOPPED = "Agent already stopped";
    protected static final String MSG_AGENT_ALREADY_RUNNING = "Agent task already in execution";

    private static final int EXECUTION_DURATION_HISTORY_SIZE = 1440;

    private final AgentConfiguration configuration;

    private State previousState;
    private State currentState;

    /*
     * Stores the date & time this agent was started (schedule)
     */
    protected Calendar startDate;
    /*
     * Stores the date & time when this agent task was last executed
     */
    protected Calendar lastExecutionDate;

    protected Duration lastExecutionDuration;

    /**
     * A history of most recent execution durations, in seconds.
     */
    private Queue<BigDecimal> executionDurationHistory = EvictingQueue.create(EXECUTION_DURATION_HISTORY_SIZE);

    /*
     * This object is used to control access to the task execution independently of other
     * operations.
     */
    private final Object runLock = new Object();
    private final Object changeLock = new Object();

    private boolean stopRequested = false;

    public Agent(AgentConfiguration configuration)
    {
        this.configuration = configuration;
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
     * @return This agent's configuration data
     */
    public AgentConfiguration getConfiguration()
    {
        return configuration;
    }

    /**
     * @return {@code true} if this agent is configured to start automatically, as in
     *         {@link AgentConfiguration}; otherwise, {@code false}.
     */
    public boolean isAutomaticallyStarted()
    {
        return configuration.isAutomaticallyStarted();
    }

    /**
     * @return {@code true} if this agent is hidden, as in {@link AgentConfiguration};
     *         otherwise, {@code false}.
     */
    public boolean isHidden()
    {
        return configuration.isHidden();
    }

    protected void setState(State currentState)
    {
        previousState = this.currentState;
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
     * @return {@code true} if this agent's timer (not its task) is currently started;
     *         otherwise {@code false}.
     */
    public boolean isStarted()
    {
        return currentState == State.STARTED || (currentState == State.RUNNING && previousState == State.STARTED);
    }

    /**
     * @return {@code true} if this agent's task is currently running; otherwise
     *         {@code false}.
     */
    public boolean isRunning()
    {
        return currentState == State.RUNNING;
    }

    /**
     * @return {@code true} if this agent's timer is currently stopped; otherwise
     *         {@code false}.
     */
    public boolean isStopped()
    {
        return currentState == State.STOPPED;
    }

    /**
     * @return The date & time this agent was started (scheduled).
     */
    public Calendar getStartDate()
    {
        return DateUtils.getClonedDate(startDate);
    }

    /**
     * @return The date & time when this agent task was last executed.
     */
    public Calendar getLastRunDate()
    {
        return DateUtils.getClonedDate(lastExecutionDate);
    }

    /**
     * Starts this agent timer considering the interval settled in this object for execution.
     */
    public final void start()
    {
        switch (getState())
        {
        case STARTED:
            throw new IllegalStateException(MSG_AGENT_ALREADY_STARTED);
        case STOPPED:
            throw new IllegalStateException("Agent was stopped. Please reset this agent before restarting");
        default:
            break;
        }
        synchronized (changeLock)
        {
            if (isStarted())
            {
                throw new IllegalStateException(MSG_AGENT_ALREADY_STARTED);
            }
            onStart();
            setState(State.STARTED);
            startDate = Calendar.getInstance();
        }
    }

    public abstract void onStart();

    /**
     * Terminates this agent timer gracefully. Does not interfere with a currently executing
     * task, if it exists.
     */
    public final void stop() throws TimeoutException
    {
        stopRequested = true;
        if (isStopped())
        {
            throw new IllegalStateException(MSG_AGENT_ALREADY_STOPPED);
        }
        synchronized (changeLock)
        {
            if (isStopped())
            {
                throw new IllegalStateException(MSG_AGENT_ALREADY_STOPPED);
            }
            LOG.info("Stopping agent...");
            int sleepSeconds = 2;
            int attempts = getStopTimeoutSeconds() / sleepSeconds;
            while (isRunning() && attempts-- > 0)
            {
                try
                {
                    LOG.info("Agent task in execution. Waiting for its completion.");
                    Thread.sleep(sleepSeconds * 1000l);
                }
                catch (InterruptedException exception)
                {
                    LOG.warn("Thread was interrupted.", exception);
                    // Restore interrupted state
                    Thread.currentThread().interrupt();
                }
            }
            if (isRunning())
            {
                throw new TimeoutException("Timeout waiting for agent task to complete. Please try again later.");
            }
            onStop();
            setState(State.STOPPED);
            startDate = null;
            LOG.info("Agent stopped successfully.");
        }
    }

    public abstract void onStop();

    /**
     * The method called by the system to execute the agent task automatically.
     */
    @Override
    public void run()
    {
        run(false);
    }

    public void run(boolean manualFlag)
    {
        if (stopRequested && !manualFlag) return;
        if (isRunning())
        {
            if (manualFlag)
            {
                throw new IllegalStateException(MSG_AGENT_ALREADY_RUNNING);
            }
            LOG.info(MSG_AGENT_ALREADY_RUNNING);
        }
        else
        {
            synchronized (runLock)
            {
                setState(State.RUNNING);
                lastExecutionDate = Calendar.getInstance();
                LOG.info("Running agent...");
                Stopwatch stopwatch = Stopwatch.createStarted(Counter.Type.WALL_CLOCK_TIME);
                try
                {
                    runTask();
                    updateStatistics(stopwatch);
                    LOG.info("Agent task finished in {}", lastExecutionDuration);
                    afterRun();
                }
                catch (Exception exception)
                {
                    LOG.error("Agent task ended with an exception", exception);
                }
                finally
                {
                    setState(previousState);
                }
            }
        }
    }

    private void updateStatistics(Stopwatch stopwatch)
    {
        lastExecutionDuration = stopwatch.elapsedTime(Counter.Type.WALL_CLOCK_TIME);
        executionDurationHistory.offer(BigDecimal.valueOf(lastExecutionDuration.toSeconds()).setScale(9));
    }

    /**
     * Calculates and formats the average of last execution durations. Returns {@code "null"}
     * if no execution is available in the local history.
     *
     * @return a string containing the average of execution durations, or {@code "null"}
     */
    protected String formatAverageExecutionDuration()
    {
        BigDecimal average = StatisticsUtils.average(executionDurationHistory);
        return average != null ? average + " second(s)" : "null";
    }

    /**
     * Formats the last execution duration for reporting.
     *
     * @return the formatted duration, or {@code "null"}
     */
    protected Object formatLastExecutionDuration()
    {
        return lastExecutionDuration != null ? lastExecutionDuration.toString(FormatStyle.SHORT) : "null";
    }

    /**
     * Implements the logic for concrete agents. This method cannot be accessed externally.
     * Its functionality will be available via the run() method.
     */
    protected abstract void runTask();

    /**
     * An event to be fired after agent task run.
     */
    protected abstract void afterRun();

    /**
     * @return This agent's stop timeout in seconds, as in {@link AgentConfiguration}. If a
     *         negative amount is configured, then {@link Integer#MAX_VALUE} will be returned.
     */
    public int getStopTimeoutSeconds()
    {
        int stopTimeoutSeconds = configuration.getStopTimeoutInSeconds();
        return stopTimeoutSeconds >= 0 ? stopTimeoutSeconds : Integer.MAX_VALUE;
    }

    /**
     * @return {@code true} if a stop request has been sent for this agent
     */
    protected boolean isStopRequested()
    {
        return stopRequested;
    }

    public abstract String getStatusString();

}
