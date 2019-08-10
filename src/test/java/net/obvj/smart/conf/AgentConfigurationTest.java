package net.obvj.smart.conf;

import static org.junit.Assert.*;

import org.junit.Test;

import net.obvj.smart.conf.xml.XmlAgent;
import net.obvj.smart.conf.xml.XmlSmart;

public class AgentConfigurationTest
{

    private static final String XML_TIMER_AGENT_30_SECONDS = "testAgents/timerAgent30seconds.xml";

    @Test
    public void testTimerAgent30Seconds()
    {
        XmlSmart xml = AgentConfiguration.loadAgentsXmlFile(XML_TIMER_AGENT_30_SECONDS);
        assertEquals(1, xml.getAgents().size());
        XmlAgent agent = xml.getAgents().get(0);
        assertEquals("net.obvj.smart.agents.dummy.DummyAgent", agent.getAgentClass());
        assertEquals("DummyAgent", agent.getName());
        assertEquals("timer", agent.getType());
        assertEquals("30 seconds", agent.getInterval());
        assertEquals(false, agent.isAutomaticallyStarted());
        assertEquals(5, agent.getStopTimeoutInSeconds());
    }

}
