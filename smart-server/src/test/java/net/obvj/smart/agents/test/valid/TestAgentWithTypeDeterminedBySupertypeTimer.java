package net.obvj.smart.agents.test.valid;

import net.obvj.smart.agents.TimerAgent;
import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.AgentTask;

@Agent()
public class TestAgentWithTypeDeterminedBySupertypeTimer extends TimerAgent
{
    @AgentTask
    @Override
    protected void runTask()
    {
    }
}
