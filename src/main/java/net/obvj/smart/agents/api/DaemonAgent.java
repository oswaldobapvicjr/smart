package net.obvj.smart.agents.api;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.conf.xml.XmlAgent;
import net.obvj.smart.util.DateUtil;

/**
 * A DaemonAgent is a process that runs constantly in background, as long as the system is
 * running.
 * 
 * Available operations are: 'start', 'stop', and 'reset' Not all concrete DaemonAgents
 * may implement the 'stop' operation.
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public abstract class DaemonAgent extends Agent
{

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final Logger logger = Logger.getLogger("smart-server");

    private Object runLock;
    
    public DaemonAgent()
    {
        this(null);
    }

    public DaemonAgent(String name)
    {
        setName(name == null ? this.getClass().getSimpleName() : name);
        setType("DAEMON");
        setState(State.SET);
        this.runLock = new Object();
    }

    /**
     * Creates a new DaemonAgent from the given XmlAgent
     * 
     * @throws ReflectiveOperationException if the agent class or constructor cannot be found,
     *                                      or the constructor is not accessible, or the agent
     *                                      cannot be instantiated
     * 
     * @since 2.0
     */
    public static Agent parseAgent(XmlAgent xmlAgent) throws ReflectiveOperationException
    {
        if (!"daemon".equals(xmlAgent.getType()))
        {
            throw new IllegalArgumentException("Not a daemon agent");
        }
        DaemonAgent agent = (DaemonAgent) Class.forName(xmlAgent.getAgentClass()).getConstructor().newInstance();
        agent.setName(xmlAgent.getName());
        agent.setStopTimeoutSeconds(xmlAgent.getStopTimeoutInSeconds());
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
                setState(State.RUNNING);
                lastRunDate = Calendar.getInstance();
                logger.log(Level.INFO, "{0} - Agent task started.", DateUtil.formatDate(lastRunDate.getTime()));
                try
                {
                    runTask();
                    setState(State.STOPPED);
                }
                catch (Exception e)
                {
                    logger.severe(e.getClass().getName() + ": " + e.getMessage());
                    setState(State.ERROR);
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
            throw new IllegalStateException("Agent already started");
        case STOPPED:
            throw new IllegalStateException("Agent has been stopped. Try to reset this agent before restarting");
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
            schedule.schedule(this, 0, TimeUnit.SECONDS);
            logger.info("Daemon agent started");
            setState(State.STARTED);
            startDate = Calendar.getInstance();
        }
    }

    /**
     * Tries to stop this agent timer gracefully, within a given stop timeout configuration in
     * seconds. If the agent execution is still on-going after the stop timeout, the stop
     * request is cancelled.
     */
    public final void stop() throws TimeoutException
    {
        if (isStopped())
        {
            throw new IllegalStateException("Agent already stopped");
        }
        synchronized (this)
        {
            if (isStopped())
            {
                throw new IllegalStateException("Agent already stopped");
            } 
            stopTask();
            schedule.shutdown();

            logger.info("Stopping agent...");
            int sleepSeconds = 2;
            int attempts = getStopTimeoutSeconds() / sleepSeconds;
            while (isRunning() && attempts-- > 0)
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
            if (isRunning())
            {
                setState(State.ERROR);
                throw new TimeoutException("Unable to stop agent task.");
            }

            setState(State.STOPPED);
            startDate = null;
            logger.info("Agent stopped successfully.");
        }
    }

    @Override
    public boolean isRunning()
    {
        return isStarted();
    }
    
    /**
     * @return A string with this agent's metadata (name, type, 'currentState' and
     *         'startDate')
     */
    public String getStatusString()
    {
        return new StringBuilder(getName()).append(" {").append(LINE_SEPARATOR).append("   type:       ").append(getType())
                .append(LINE_SEPARATOR).append("   status:     ").append(getState()).append(LINE_SEPARATOR)
                .append("   startDate:  ").append(startDate != null ? DateUtil.formatDate(startDate.getTime()) : "null")
                .append(LINE_SEPARATOR).append("}").toString();
    }

    /**
     * Implements the logic for concrete agents. This method cannot be accessed externally.
     * Its functionality will be available via the run() method.
     */
    protected abstract void runTask();

    protected abstract void stopTask();

}
