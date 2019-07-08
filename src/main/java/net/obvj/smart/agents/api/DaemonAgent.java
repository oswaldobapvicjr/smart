package net.obvj.smart.agents.api;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import net.obvj.smart.util.DateUtil;

/**
 * A DaemonAgent is a process that runs constantly in background, as long as the system is
 * running.
 * 
 * Available operations are: 'start', 'stop', and 'reset' Not all concrete DaemonAgents
 * may implement the 'stop' operation.
 * 
 */
public abstract class DaemonAgent extends Agent
{

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final Logger logger = Logger.getLogger(DaemonAgent.class.getName());

    public DaemonAgent()
    {
        this(null);
    }

    public DaemonAgent(String name)
    {
        this.name = (name == null ? this.getClass().getSimpleName() : name);
        this.type = "DAEMON";
        this.currentState = State.SET;
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
            synchronized (this)
            {
                currentState = State.RUNNING;
                lastRunDate = Calendar.getInstance();
                logger.info(String.format("%s - Agent task started.", DateUtil.formatDate(lastRunDate.getTime())));
                try
                {
                    runTask();
                }
                catch (Exception e)
                {
                    logger.severe(e.getClass().getName() + ": " + e.getMessage());
                    currentState = State.ERROR;
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
            logger.info("Starting agent...");
            // timer.schedule(this, 0);
            schedule.schedule(this, 0, TimeUnit.SECONDS);
            logger.info("Daemon agent started");
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
            stopTask();
            schedule.shutdown();

            logger.info("Stopping agent...");
            int sleepSeconds = 2;
            int attempts = getStopTimeoutSeconds() / sleepSeconds;
            while ((currentState == State.RUNNING) && (attempts-- > 0))
            {
                try
                {
                    logger.info("Agent task in execution. Waiting for its completion.");
                    Thread.sleep(sleepSeconds * 1000);
                }
                catch (InterruptedException e)
                {
                    logger.severe("InterruptedException: " + e.getMessage());
                }
            }
            if (currentState == State.RUNNING)
            {
                currentState = State.ERROR;
                throw new TimeoutException("Unable to stop agent task.");
            }

            currentState = State.STOPPED;
            startDate = null;
            logger.info("Agent stopped successfully.");
        }
    }

    /**
     * @return A string with this agent's metadata (name, type, 'currentState' and
     *         'startDate')
     */
    public String getStatusString()
    {
        return new StringBuilder(name).append(" {").append(LINE_SEPARATOR).append("   type:       ").append("DAEMON")
                .append(LINE_SEPARATOR).append("   status:     ").append(currentState).append(LINE_SEPARATOR)
                .append("   startDate:  ").append(startDate != null ? DateUtil.formatDate(startDate.getTime()) : "null")
                .append(LINE_SEPARATOR).append("}").toString();
    }

    /**
     * Implements the logic for concrete agents. This method cannot be accessed externally.
     * Its functionality will be available via the run() method.
     */
    protected abstract void runTask();

    protected abstract void stopTask();

    public abstract int getStopTimeoutSeconds();
}
