package net.obvj.smart.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.obvj.smart.agents.Agent;

/**
 * A Runnable object that may be executed before JVM termination for graceful system
 * shutdown. The logic herein is ignored if JVM receives a kill -9 (FORCE) signal.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class ShutdownHook extends SmartServerSupport implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger("smart-server");

    @Override
    public void run()
    {
        LOG.info("Starting shutdown sequence...");
        stopAllAgents();
        closeClassicManagementConsole();
        LOG.info("Shutdown sequence complete.");
    }

    private void stopAllAgents()
    {
        LOG.info("Stopping agents...");
        for (Agent agent : agentManager.getAgents())
        {
            try
            {
                agent.stop();
            }
            catch (Exception exception)
            {
                LOG.error("Unable to gracefully stop {} due to an exception.", agent.getName(), exception);
            }
        }
    }

}
