package net.obvj.smart.agents.api;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.conf.xml.AgentConfiguration;
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
    private static final String DAEMON = "DAEMON";
    private static final Logger LOG = Logger.getLogger("smart-server");

    protected static final String MSG_AGENT_ALREADY_STARTED = "Agent already started";
    protected static final String MSG_AGENT_ALREADY_STOPPED = "Agent already stopped";

    private Object runLock;
    
    public DaemonAgent()
    {
        this(null);
    }

    public DaemonAgent(String name)
    {
        setName(name == null ? this.getClass().getSimpleName() : name);
        setType(DAEMON);
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
        if (!DAEMON.equalsIgnoreCase(configuration.getType()))
        {
            throw new IllegalArgumentException("Not a daemon agent");
        }
        DaemonAgent agent = (DaemonAgent) Class.forName(configuration.getAgentClass()).getConstructor().newInstance();
        agent.setConfiguration(configuration);
        agent.setName(configuration.getName());
        agent.setStopTimeoutSeconds(configuration.getStopTimeoutInSeconds());
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
                setState(State.RUNNING);
                lastRunDate = Calendar.getInstance();
                LOG.log(Level.INFO, "{0} - Agent task started.", DateUtil.formatDate(lastRunDate.getTime()));
                try
                {
                    runTask();
                    setState(State.STOPPED);
                }
                catch (Exception e)
                {
                    LOG.severe(e.getClass().getName() + ": " + e.getMessage());
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
            throw new IllegalStateException(MSG_AGENT_ALREADY_STARTED);
        case STOPPED:
            throw new IllegalStateException("Agent has been stopped. Try to reset this agent before restarting");
        default:
            break;
        }
        synchronized (this)
        {
            if (isStarted())
            {
                throw new IllegalStateException(MSG_AGENT_ALREADY_STARTED);
            }
            LOG.info("Starting agent...");
            schedule.schedule(this, 0, TimeUnit.SECONDS);
            LOG.info("Daemon agent started");
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
            throw new IllegalStateException(MSG_AGENT_ALREADY_STOPPED);
        }
        synchronized (this)
        {
            if (isStopped())
            {
                throw new IllegalStateException(MSG_AGENT_ALREADY_STOPPED);
            } 
            stopTask();
            schedule.shutdown();

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
                setState(State.ERROR);
                throw new TimeoutException("Unable to stop agent task.");
            }

            setState(State.STOPPED);
            startDate = null;
            LOG.info("Agent stopped successfully.");
        }
    }

    @Override
    public boolean isRunning()
    {
        return super.isStarted() || super.isRunning();
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
