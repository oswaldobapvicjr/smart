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
        LOG.log(Level.INFO, "TOTAL_MEMORY={0}", Runtime.getRuntime().totalMemory());
        LOG.log(Level.INFO, "FREE_MEMORY={0}", Runtime.getRuntime().freeMemory());
    }
}
