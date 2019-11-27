package net.obvj.smart.agents.test;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.Type;

@Agent(name = "name1", type = Type.TIMER, interval = "90 seconds", automaticallyStarted = false, hidden = true, stopTimeoutInSeconds = 99)
public class TestAgentWithAllCustomParams
{
}
