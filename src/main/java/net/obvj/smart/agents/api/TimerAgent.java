package net.obvj.smart.agents.api;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.conf.xml.AgentConfiguration;
import net.obvj.smart.util.DateUtil;
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

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String TIMER = "TIMER";
    private static final Logger LOG = Logger.getLogger("smart-server");

    protected static final String MSG_AGENT_ALREADY_STARTED = "Agent already started";
    protected static final String MSG_AGENT_ALREADY_STOPPED = "Agent already stopped";

    private int interval;
    private TimeUnit timeUnit;

    /*
     * This object is used to control access to the task execution independently of other
     * operations.
     */
    private final Object runLock;

    public TimerAgent()
    {
        this(null, 1, TimeUnit.DEFAULT);
    }

    public TimerAgent(String name, int interval, TimeUnit timeUnit)
    {
        setName(name == null ? this.getClass().getSimpleName() : name);
        setType(TIMER);
        this.interval = interval;
        this.timeUnit = timeUnit;
        setState(State.SET);
        this.runLock = new Object();
    }

    /**
     * Creates a new DaemonAgent from the given configuration.
     * 
     * @throws ReflectiveOperationException if the agent class or constructor cannot be found,
     *                                      or the constructor is not accessible, or the agent
     *                                      cannot be instantiated
     * 
     * @since 2.0
     */
    public static Agent parseAgent(AgentConfiguration configuration) throws ReflectiveOperationException
    {
        if (!TIMER.equalsIgnoreCase(configuration.getType()))
        {
            throw new IllegalArgumentException("Not a timer agent");
        }

        TimerAgent agent = (TimerAgent) Class.forName(configuration.getAgentClass()).getConstructor().newInstance();
        agent.setConfiguration(configuration);
        agent.setName(configuration.getName());
        agent.setStopTimeoutSeconds(configuration.getStopTimeoutInSeconds());

        TimeInterval timeInterval = TimeInterval.of(configuration.getInterval());
        agent.interval = timeInterval.getDuration();
        agent.timeUnit = timeInterval.getTimeUnit();

        return agent;
    }

    /**
     * Executes this agent task.
     */
    public void run()
    {
        if (isRunning())
        {
            LOG.info("Agent task already in execution.");
        }
        else
        {
            synchronized (runLock)
            {
                State previousState = getState();
                setState(State.RUNNING);
                lastRunDate = Calendar.getInstance();
                LOG.log(Level.INFO, "{0} - Agent task started.", DateUtil.formatDate(lastRunDate.getTime()));
                try
                {
                    runTask();
                }
                catch (Exception e)
                {
                    LOG.severe(e.getClass().getName() + ": " + e.getMessage());
                }
                finally
                {
                    setState(previousState);
                    LOG.log(Level.INFO, "{0} - Agent task complete.", DateUtil.now());
                }
            }
        }
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
        synchronized (this)
        {
            if (isStarted())
            {
                throw new IllegalStateException(MSG_AGENT_ALREADY_STARTED);
            }
            LOG.log(Level.INFO, "Starting agent: {0}", getName());
            Date start = DateUtil.getExactStartDateEvery(interval, timeUnit);

            schedule.scheduleAtFixedRate(this, (start.getTime() - System.currentTimeMillis()),
                    timeUnit.toMillis(interval), java.util.concurrent.TimeUnit.MILLISECONDS);

            LOG.log(Level.INFO, "Agent {0} scheduled to run every {1} {2}. Start programmed to {3}",
                    new Object[] { getName(), interval, timeUnit, DateUtil.formatDate(start) });
            setState(State.STARTED);
            startDate = Calendar.getInstance();
        }
    }

    /**
     * Terminates this agent timer gracefully. Does not interfere with a currently executing
     * task, if it exists.
     */
    public final void stop() throws TimeoutException
    {
        if (isStopped())
        {
            throw new IllegalStateException(MSG_AGENT_ALREADY_STOPPED);
        }
        synchronized (this)
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
                    wait(sleepSeconds * 1000l);
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
     * @return A string with this agent's metadata (name, 'started' flag, 'running' flag,
     *         'startDate' and 'lastRunDate')
     */
    public String getStatusString()
    {

        return new StringBuilder(getName()).append(" {").append(LINE_SEPARATOR).append("   type:       ").append(getType())
                .append(LINE_SEPARATOR).append("   status:     ").append(getState()).append(LINE_SEPARATOR)
                .append("   startDate:  ").append(startDate != null ? DateUtil.formatDate(startDate.getTime()) : "null")
                .append(LINE_SEPARATOR).append("   lastRun:    ")
                .append(lastRunDate != null ? DateUtil.formatDate(lastRunDate.getTime()) : "null")
                .append(LINE_SEPARATOR).append("   frequency:  ").append(interval).append(" ").append(timeUnit)
                .append(LINE_SEPARATOR).append("}").toString();
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
