package net.obvj.smart.agents.test;

import net.obvj.smart.agents.api.TimerAgent;
import net.obvj.smart.conf.annotation.Agent;

@Agent()
public class TestAgentWithTypeDeterminedBySupertypeTimer extends TimerAgent
{
    @Override
    protected void runTask()
    {
    }
}
