package net.obvj.smart.agents.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.conf.xml.XmlAgent;
import net.obvj.smart.util.DateUtil;
import net.obvj.smart.util.DateUtil.TimeUnit;
import net.obvj.smart.util.TimeInterval;

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
    private static final Logger logger = Logger.getLogger("smart-server");

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

    public TimerAgent(String name)
    {
        this(name, 1, TimeUnit.DEFAULT);
    }

    public TimerAgent(String name, int interval, TimeUnit timeUnit)
    {
        this.name = (name == null ? this.getClass().getSimpleName() : name);
        this.type = "TIMER";
        this.interval = interval;
        this.timeUnit = timeUnit;
        this.currentState = State.SET;
        this.runLock = new Object();
    }

    /**
     * Creates a new DaemonAgent from the given XmlAgent
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
        if (!"timer".equals(xmlAgent.getType()))
        {
            throw new IllegalArgumentException("Not a timer agent");
        }

        TimerAgent agent = (TimerAgent) Class.forName(xmlAgent.getAgentClass()).getConstructor().newInstance();
        agent.name = xmlAgent.getName();
        agent.stopTimeoutSeconds = xmlAgent.getStopTimeoutInSeconds();

        TimeInterval timeInterval = TimeInterval.of(xmlAgent.getInterval());
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
            logger.info("Agent task already in execution.");
        }
        else
        {
            synchronized (runLock)
            {
                State previousState = currentState;
                currentState = State.RUNNING;
                lastRunDate = Calendar.getInstance();
                logger.log(Level.INFO, "{0} - Agent task started.", DateUtil.formatDate(lastRunDate.getTime()));
                try
                {
                    runTask();
                }
                catch (Exception e)
                {
                    logger.severe(e.getClass().getName() + ": " + e.getMessage());
                }
                finally
                {
                    currentState = previousState;
                    logger.log(Level.INFO, "{0} - Agent task complete.", DateUtil.now());
                }
            }
        }
    }

    /**
     * Starts this agent timer considering the interval settled in this object for execution.
     */
    public final void start()
    {
        switch (currentState)
        {
        case STARTED:
            throw new IllegalStateException("Agent already started");
        case STOPPED:
            throw new IllegalStateException("Illegal state: agent task already cancelled");
        default:
            break;
        }
        synchronized (this)
        {
            if (isStarted())
            {
                throw new IllegalStateException("Agent already started");
            }
            logger.log(Level.INFO, "Starting agent: {0}", name);
            Date start = DateUtil.getExactStartDateEvery(interval, timeUnit);

            schedule.scheduleAtFixedRate(this, (start.getTime() - System.currentTimeMillis()),
                    timeUnit.toMillis(interval), java.util.concurrent.TimeUnit.MILLISECONDS);

            logger.log(Level.INFO, "Agent {0} scheduled to run every {1} {2}. Start programmed to {3}",
                    new Object[] { name, interval, timeUnit, DateUtil.formatDate(start) });
            currentState = State.STARTED;
            startDate = Calendar.getInstance();
        }
    }

    /**
     * Terminates this agent timer gracefully. Does not interfere with a currently executing
     * task, if it exists.
     */
    public final void stop() throws TimeoutException
    {
        if (currentState == State.STOPPED)
        {
            throw new IllegalStateException("Agent already stopped");
        }
        synchronized (this)
        {
            if (currentState == State.STOPPED)
            {
                throw new IllegalStateException("Agent already stopped");
            }
            logger.info("Stopping agent...");
            int sleepSeconds = 2;
            int attempts = getStopTimeoutSeconds() / sleepSeconds;
            while ((currentState == State.RUNNING) && (attempts-- > 0))
            {
                try
                {
                    logger.info("Agent task in execution. Waiting for its completion.");
                    wait(sleepSeconds * 1000l);
                }
                catch (InterruptedException e)
                {
                    logger.log(Level.WARNING, "Thread was interrupted.", e);
                    // Restore interrupted state
                    Thread.currentThread().interrupt();
                }
            }
            if (currentState == State.RUNNING)
            {
                throw new TimeoutException("Unable to stop agent task.");
            }
            schedule.shutdown();
            currentState = State.STOPPED;
            startDate = null;
            logger.info("Agent stopped successfully.");
        }
    }

    /**
     * @return A string with this agent's metadata (name, 'started' flag, 'running' flag,
     *         'startDate' and 'lastRunDate')
     */
    public String getStatusString()
    {

        return new StringBuilder(name).append(" {").append(LINE_SEPARATOR).append("   type:       ").append(type)
                .append(LINE_SEPARATOR).append("   status:     ").append(currentState).append(LINE_SEPARATOR)
                .append("   startDate:  ").append(startDate != null ? DateUtil.formatDate(startDate.getTime()) : "null")
                .append(LINE_SEPARATOR).append("   lastRun:    ")
                .append(lastRunDate != null ? DateUtil.formatDate(lastRunDate.getTime()) : "null")
                .append(LINE_SEPARATOR).append("   frequency:  ").append(interval).append(" ").append(timeUnit)
                .append(LINE_SEPARATOR).append("}").toString();
    }

    /**
     * Implements the logic for concrete agents. This method cannot be accessed externally.
     * Its functionality will be available via the run() method.
     */
    protected abstract void runTask();

}
