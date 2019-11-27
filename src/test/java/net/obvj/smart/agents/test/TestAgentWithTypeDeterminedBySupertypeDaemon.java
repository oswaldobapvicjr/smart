package net.obvj.smart.agents.test;

import net.obvj.smart.agents.api.DaemonAgent;
import net.obvj.smart.conf.annotation.Agent;

@Agent()
public class TestAgentWithTypeDeterminedBySupertypeDaemon extends DaemonAgent
{
    @Override
    protected void runTask()
    {
    }

    @Override
    protected void stopTask()
    {
    }
}
