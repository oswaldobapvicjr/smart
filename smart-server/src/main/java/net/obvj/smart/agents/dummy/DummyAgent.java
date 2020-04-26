package net.obvj.smart.agents.dummy;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.AgentTask;
import net.obvj.smart.conf.annotation.Type;

/**
 * A dummy agent that executes for 5 seconds for testing purposes
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
@Agent(type = Type.CRON)
public class DummyAgent
{
    private static final Logger LOG = Logger.getLogger("smart-server");

    @AgentTask
    public void runTask()
    {
        LOG.log(Level.INFO, "DummyAgent says: \"Hello!\"");
    }

}
