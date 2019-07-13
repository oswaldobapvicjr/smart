package net.obvj.smart.main;

import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.console.ManagementConsole;
import net.obvj.smart.manager.AgentManager;

/**
 * A Runnable object that may be executed before JVM termination for graceful system
 * shutdown. The logic herein is ignored if JVM receives a kill -9 (FORCE) signal.
 */
public class ShutdownHook implements Runnable
{

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public void run()
    {
        logger.info("Starting shutdown sequence...");

        /*
         * STEP 1: Stop agents
         */
        logger.info("Stopping agents...");
        for (Agent agent : AgentManager.getInstance().getAgents())
        {
            try
            {
                agent.stop();

            }
            catch (IllegalStateException | TimeoutException | UnsupportedOperationException ex)
            {
                logger.warning(agent.getName() + ": " + ex.getMessage());

            }
            catch (Exception ex)
            {
                logger.severe(agent.getName() + ": " + ex.getMessage());
            }
        }

        /*
         * STEP 2: Close Management Console
         */
        logger.info("Closing Agent Management Console...");
        ManagementConsole.getInstance().stop();

        logger.info("Shutdown sequence complete.");
    }
}
