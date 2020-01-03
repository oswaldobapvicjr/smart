package net.obvj.smart.agents.test.valid;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.AgentTask;

/**
 * A valid dummy agent without specific type in annotation
 *
 * @author oswaldo.bapvic.jr
 */
@Agent
public class DummyAgent
{
    @AgentTask
    public void runTask()
    {
    }

}
