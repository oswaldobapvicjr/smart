package net.obvj.smart.agents.dummy;

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
   
    @Override
    protected void runTask()
    {
        LOG.info("DummyDaemonAgent says hello.");
    }

    @Override
    protected void stopTask()
    {
        LOG.info("DummyDaemonAgent says good bye.");
    }

}
