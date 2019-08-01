package net.obvj.smart.agents.dummy;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.agents.api.TimerAgent;

/**
 * A dummy agent that appends some runtime information to the log, sleeping between each
 * counter for testing purposes
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class PMReporter extends TimerAgent
{
    private static final Logger LOG = Logger.getLogger("smart-server");
    
    @Override
    protected void runTask()
    {
        Runtime rt = Runtime.getRuntime();
        LOG.info("Loading statistics...");
        try
        {
            Thread.sleep(1000);
            LOG.log(Level.INFO, "AVAILABLE_PROCESSORS={0}", rt.availableProcessors());
            Thread.sleep(1000);
            LOG.log(Level.INFO, "TOTAL_MEMORY={0}", rt.totalMemory());
            Thread.sleep(1000);
            LOG.log(Level.INFO, "FREE_MEMORY={0}", rt.freeMemory());
            Thread.sleep(1000);
            LOG.log(Level.INFO, "MAX_MEMORY={0}", rt.maxMemory());

        }
        catch (InterruptedException e)
        {
            LOG.log(Level.WARNING, "Interrupted while sleeping", e);
            Thread.currentThread().interrupt();
        }
    }
}
