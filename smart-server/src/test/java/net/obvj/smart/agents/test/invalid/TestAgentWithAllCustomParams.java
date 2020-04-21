package net.obvj.smart.agents.test.invalid;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.Type;

@Agent(name = "name1", type = Type.TIMER, frequency = "90 seconds", automaticallyStarted = false, hidden = true, stopTimeoutInSeconds = 99)
public class TestAgentWithAllCustomParams
{
    //INVALID: Missing @AgentTask method
}
