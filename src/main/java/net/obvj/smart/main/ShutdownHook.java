package net.obvj.smart.main;

import java.util.logging.Logger;

import net.obvj.smart.agents.api.Agent;

/**
 * A Runnable object that may be executed before JVM termination for graceful system
 * shutdown. The logic herein is ignored if JVM receives a kill -9 (FORCE) signal.
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class ShutdownHook extends SmartServerSupport implements Runnable
{
    private final Logger logger = Logger.getLogger("smart-server");

    public void run()
    {
        logger.info("Starting shutdown sequence...");
        stopAllAgents();
        closeClassicManagementConsole();
        logger.info("Shutdown sequence complete.");
    }

    private void stopAllAgents()
    {
        logger.info("Stopping agents...");
        for (Agent agent : agentManager.getAgents())
        {
            try
            {
                agent.stop();
            }
            catch (Exception ex)
            {
                logger.severe(agent.getName() + ": " + ex.getMessage());
            }
        }
    }

}
