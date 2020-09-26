package net.obvj.smart.agents.test.valid;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.AgentTask;
import net.obvj.smart.conf.annotation.Type;

@Agent(type = Type.CRON)
public class TestAgentWithNoNameAndTypeCronAndAgentTask
{
    @AgentTask
    public void cronTaskMethod()
    {
        System.out.println("cronTaskMethod() called");
    }
}
