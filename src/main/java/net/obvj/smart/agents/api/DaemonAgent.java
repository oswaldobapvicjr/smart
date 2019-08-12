package net.obvj.smart.agents.api;

import java.lang.reflect.InvocationTargetException;
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
        if (!"daemon".equals(xmlAgent.getType()))
        {
            throw new IllegalArgumentException("Not a daemon agent");
        }
        DaemonAgent agent = (DaemonAgent) Class.forName(xmlAgent.getAgentClass()).getConstructor().newInstance();
        agent.name = xmlAgent.getName();
        agent.stopTimeoutSeconds = xmlAgent.getStopTimeoutInSeconds();
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
            synchronized (this)
            {
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
            currentState = State.STARTED;
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
        return new StringBuilder(name).append(" {").append(LINE_SEPARATOR).append("   type:       ").append(type)
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

}
