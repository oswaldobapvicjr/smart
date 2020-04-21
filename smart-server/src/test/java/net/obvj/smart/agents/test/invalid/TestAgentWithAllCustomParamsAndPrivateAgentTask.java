package net.obvj.smart.agents.test.invalid;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.AgentTask;
import net.obvj.smart.conf.annotation.Type;

@Agent(name = "name1", type = Type.TIMER, frequency = "90 seconds", automaticallyStarted = false, hidden = true, stopTimeoutInSeconds = 99)
public class TestAgentWithAllCustomParamsAndPrivateAgentTask
{
    @AgentTask
    // INVALID: Agent constructor should not be private
    private void agentTask()
    {
    }
}
