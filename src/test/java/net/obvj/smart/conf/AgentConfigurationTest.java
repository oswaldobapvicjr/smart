package net.obvj.smart.conf;

import static net.obvj.smart.TestUtil.assertException;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import javax.xml.bind.UnmarshalException;

import org.junit.Test;

import net.obvj.smart.conf.xml.XmlAgent;
import net.obvj.smart.conf.xml.XmlSmart;

/**
 * Unit tests for the {@link AgentConfiguration} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentConfigurationTest
{
    // Test files
    private static final String XML_TIMER_AGENT_30_SECONDS = "testAgents/timerAgent30seconds.xml";
    private static final String XML_TIMER_AGENT_WITH_DEFAULT_VALUES = "testAgents/timerAgentWithDefaultValues.xml";
    private static final String XML_TWO_AGENTS = "testAgents/twoAgents.xml";

    // Test data
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String DUMMY_AGENT_CLASS = "net.obvj.smart.agents.dummy.DummyAgent";
    private static final String TIMER = "timer";

    private static final String DUMMY_DAEMON = "DummyDaemon";
    private static final String DUMMY_DAEMON_CLASS = "net.obvj.smart.agents.dummy.DummyDaemonAgent";
    private static final String DAEMON = "daemon";

    @Test
    public void testLoadTimerAgent30Seconds()
    {
        XmlSmart xml = AgentConfiguration.loadAgentsXmlFile(XML_TIMER_AGENT_30_SECONDS);
        assertEquals(1, xml.getAgents().size());
        XmlAgent agent = xml.getAgents().get(0);
        assertEquals(DUMMY_AGENT, agent.getName());
        assertEquals(DUMMY_AGENT_CLASS, agent.getAgentClass());
        assertEquals(TIMER, agent.getType());
        assertEquals("30 seconds", agent.getInterval());
        assertEquals(false, agent.isAutomaticallyStarted());
        assertEquals(5, agent.getStopTimeoutInSeconds());
    }

    @Test
    public void testLoadTimerAgentDefaultValues()
    {
        XmlSmart xml = AgentConfiguration.loadAgentsXmlFile(XML_TIMER_AGENT_WITH_DEFAULT_VALUES);
        assertEquals(1, xml.getAgents().size());
        XmlAgent agent = xml.getAgents().get(0);
        assertEquals(DUMMY_AGENT, agent.getName());
        assertEquals(DUMMY_AGENT_CLASS, agent.getAgentClass());
        assertEquals(TIMER, agent.getType());
        assertEquals("1", agent.getInterval());
        assertEquals(true, agent.isAutomaticallyStarted());
        assertEquals(-1, agent.getStopTimeoutInSeconds());
    }

    @Test
    public void testLoadTimerAgentWithoutName()
    {
        assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
                () -> AgentConfiguration.loadAgentsXmlFile("testAgents/timerAgentWithoutName.xml"));
    }

    @Test
    public void testLoadAgentWithoutType()
    {
        assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
                () -> AgentConfiguration.loadAgentsXmlFile("testAgents/agentWithoutType.xml"));
    }

    @Test
    public void testLoadTimerAgentWithoutClass()
    {
        assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
                () -> AgentConfiguration.loadAgentsXmlFile("testAgents/timerAgentWithoutClass.xml"));
    }

    @Test
    public void testLoadTimerAgentWithInvalidName()
    {
        assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
                () -> AgentConfiguration.loadAgentsXmlFile("testAgents/timerAgentWithInvalidName.xml"));
    }

    @Test
    public void testLoadAgentWithInvalidType()
    {
        assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
                () -> AgentConfiguration.loadAgentsXmlFile("testAgents/agentWithInvalidType.xml"));
    }

    @Test
    public void testAgentsFileNotFound()
    {
        assertException(AgentConfigurationException.class, null, FileNotFoundException.class,
                () -> AgentConfiguration.loadAgentsXmlFile("testAgents/notfound.xml"));
    }
    
    @Test
    public void testLoadXmlWithTwoAgents()
    {
        AgentConfiguration config = new AgentConfiguration(XML_TWO_AGENTS);
        assertEquals(2, config.getAgents().size());

        XmlAgent dummyAgent = config.getAgentConfiguration(DUMMY_AGENT);
        assertEquals(DUMMY_AGENT, dummyAgent.getName());
        assertEquals(TIMER, dummyAgent.getType());
        assertEquals(DUMMY_AGENT_CLASS, dummyAgent.getAgentClass());
        assertEquals("30 seconds", dummyAgent.getInterval());

        XmlAgent dummyDaemon = config.getAgentConfiguration(DUMMY_DAEMON);
        assertEquals(DUMMY_DAEMON, dummyDaemon.getName());
        assertEquals(DAEMON, dummyDaemon.getType());
        assertEquals(DUMMY_DAEMON_CLASS, dummyDaemon.getAgentClass());
    }
}
