package net.obvj.smart.agents.test.invalid;

import net.obvj.smart.conf.annotation.Agent;

// No type set; "timer" will be considered
@Agent()
public class TestAgentWithNoType
{
    // INVALID: Missing @AgentTask method
}
