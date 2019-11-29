package net.obvj.smart.agents.test.invalid;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.Type;

@Agent(type = Type.TIMER)
public class TestAgentWithNoNameAndTypeTimerAndNoAgentTask
{
    //INVALID: Missing @AgentTask method
}
