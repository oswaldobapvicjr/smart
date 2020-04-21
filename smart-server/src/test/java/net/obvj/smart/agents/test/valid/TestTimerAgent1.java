package net.obvj.smart.agents.test.valid;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.AgentTask;
import net.obvj.smart.conf.annotation.Type;

@Agent(type = Type.TIMER, frequency = "29 seconds", hidden = true)
public class TestTimerAgent1
{
    @AgentTask
    public void timerTaskMethod()
    {
        System.out.println("timerTaskMethod() called");
    }
}
