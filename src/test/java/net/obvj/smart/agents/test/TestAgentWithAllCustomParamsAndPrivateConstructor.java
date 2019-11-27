package net.obvj.smart.agents.test;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.AgentTask;
import net.obvj.smart.conf.annotation.Type;

@Agent(name = "name1", type = Type.TIMER, interval = "90 seconds", automaticallyStarted = false, hidden = true, stopTimeoutInSeconds = 99)
public class TestAgentWithAllCustomParamsAndPrivateConstructor
{
    // INVALID: Agent constructor should not be private
    private TestAgentWithAllCustomParamsAndPrivateConstructor()
    {
    }
    
    @AgentTask
    public void agentTask()
    {
    }
}
