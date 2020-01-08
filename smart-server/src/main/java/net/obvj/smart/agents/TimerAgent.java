package net.obvj.smart.agents;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.util.DateUtils;
import net.obvj.smart.util.TimeInterval;
import net.obvj.smart.util.TimeUnit;

/**
 * A thread-safe extensible Agent for tasks that are scheduled in the system to run
 * repeatedly, given an interval that is particular to each task. Available operations
 * are: 'start', 'stop', 'run' and 'reset'
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public abstract class TimerAgent extends Agent
{
    public static final String TYPE = "timer";

    private static final Logger LOG = Logger.getLogger("smart-server");

    protected static final String MSG_AGENT_ALREADY_STARTED = "Agent already started";
    protected static final String MSG_AGENT_ALREADY_STOPPED = "Agent already stopped";
    protected static final String MSG_AGENT_ALREADY_RUNNING = "Agent task already in execution";

    private int interval;
    private TimeUnit timeUnit;

    /*
     * This object is used to control access to the task execution independently of other
     * operations.
     */
    private final Object runLock = new Object();
    private final Object changeLock = new Object();

    private boolean stopRequested = false;

    /**
     * Builds a {@link TimerAgent} from the given configuration.
     *
     * @param configuration the {@link AgentConfiguration} to be set
     */
    public TimerAgent(AgentConfiguration configuration)
    {
        super(configuration);

        if (!TYPE.equalsIgnoreCase(configuration.getType()))
        {
            throw new IllegalArgumentException("Not a timer agent");
        }

        TimeInterval timeInterval = TimeInterval.of(configuration.getInterval());
        this.interval = timeInterval.getDuration();
        this.timeUnit = timeInterval.getTimeUnit();
        setState(State.SET);
    }

    /**
     * The method called by the Executor Service to execute the agent task.
     */
    @Override
    public void run()
    {
        run(false);
    }

    @Override
    public void run(boolean manualFlag)
    {
        if (stopRequested && !manualFlag) return;
        if (isRunning())
        {
            if (manualFlag)
            {
                throw new IllegalStateException(MSG_AGENT_ALREADY_RUNNING);
            }
            LOG.fine(MSG_AGENT_ALREADY_RUNNING);
        }
        else
        {
            synchronized (runLock)
            {
                State previousState = getState();
                setState(State.RUNNING);
                lastRunDate = Calendar.getInstance();
                LOG.log(Level.FINEST, "Agent task started");
                try
                {
                    runTask();
                }
                catch (Exception e)
                {
                    LOG.log(Level.SEVERE, "Agent task ended with an exception", e);
                }
                finally
                {
                    setState(previousState);
                    LOG.log(Level.FINEST, "Agent task complete.");
                }
            }
        }
    }

    /**
     * Starts this agent timer considering the interval settled in this object for execution.
     */
    @Override
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
            LOG.log(Level.INFO, "Starting agent: {0}", getName());
            Date start = DateUtils.getExactStartDateEvery(interval, timeUnit);

            schedule.scheduleAtFixedRate(this, (start.getTime() - System.currentTimeMillis()),
                    timeUnit.toMillis(interval), java.util.concurrent.TimeUnit.MILLISECONDS);

            LOG.log(Level.INFO, "Agent {0} scheduled to run every {1} {2}. Start programmed to {3}",
                    new Object[] { getName(), interval, timeUnit, DateUtils.formatDate(start) });
            setState(State.STARTED);
            startDate = Calendar.getInstance();
        }
    }

    /**
     * Terminates this agent timer gracefully. Does not interfere with a currently executing
     * task, if it exists.
     */
    @Override
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
                catch (InterruptedException e)
                {
                    LOG.log(Level.WARNING, "Thread was interrupted.", e);
                    // Restore interrupted state
                    Thread.currentThread().interrupt();
                }
            }
            if (isRunning())
            {
                throw new TimeoutException("Timeout waiting for agent task to complete. Please try again later.");
            }
            schedule.shutdown();
            setState(State.STOPPED);
            startDate = null;
            LOG.info("Agent stopped successfully.");
        }
    }

    /**
     * @return A string with current agent status in JSON format
     */
    @Override
    public String getStatusString()
    {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
        builder.append("name", getName())
               .append("type", getType())
               .append("status", getState())
               .append("startDate", (DateUtils.formatDate(startDate)))
               .append("lastRunDate", (DateUtils.formatDate(lastRunDate)))
               .append("frequency", getInterval() + " " + getTimeUnit());
        return builder.build();
    }

    public int getInterval()
    {
        return interval;
    }

    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    /**
     * Implements the logic for concrete agents. This method cannot be accessed externally.
     * Its functionality will be available via the run() method.
     */
    protected abstract void runTask();

}
