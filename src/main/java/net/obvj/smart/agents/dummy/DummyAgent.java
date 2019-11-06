package net.obvj.smart.agents.dummy;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.agents.api.TimerAgent;

/**
 * A dummy agent that executes for 5 seconds for testing purposes
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class DummyAgent extends TimerAgent
{
    private static final Logger LOG = Logger.getLogger("smart-server");
   
    @Override
    protected void runTask()
    {
        for (int i = 5; i >= 0; i--)
        {
            try
            {
                LOG.log(Level.INFO, "DummyAgent says {0}", i);
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

}
