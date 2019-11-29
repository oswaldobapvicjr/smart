package net.obvj.smart.agents.test.invalid;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.Type;

@Agent(name = "name1", type = Type.TIMER)
public class TestAgentWithCustomNameAndType
{
    //INVALID: Missing @AgentTask method
}
