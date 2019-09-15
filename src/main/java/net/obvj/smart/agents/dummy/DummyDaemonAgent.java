package net.obvj.smart.agents.dummy;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.agents.api.DaemonAgent;

/**
 * A dummy daemon agent for testing purposes
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class DummyDaemonAgent extends DaemonAgent
{
    private static final Logger LOG = Logger.getLogger("smart-server");
   
    private boolean runFlag = true;
    
    @Override
    protected void runTask()
    {
        while (runFlag)
        {
            try
            {
                LOG.info("DummyDaemonAgent says hello.");
                Thread.sleep(5000);
            }
            catch (InterruptedException e)
            {
                LOG.log(Level.WARNING, "Interrupted while sleeping", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    protected void stopTask()
    {
        runFlag = false;
    }

}
