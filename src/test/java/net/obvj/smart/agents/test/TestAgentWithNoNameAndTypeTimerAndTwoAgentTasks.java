package net.obvj.smart.agents.test;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.AgentTask;
import net.obvj.smart.conf.annotation.Type;

@Agent(type = Type.TIMER)
public class TestAgentWithNoNameAndTypeTimerAndTwoAgentTasks
{    
    @AgentTask
    public void timerTaskMethod()
    {
        System.out.println("timerTaskMethod() called");
    }
    
    // INVALID: only one method with @AgentTask allowed
    @AgentTask
    public void timerTaskMethod2()
    {
        System.out.println("timerTaskMethod2() called");
    }
}
