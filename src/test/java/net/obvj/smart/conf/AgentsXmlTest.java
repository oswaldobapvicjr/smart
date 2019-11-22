package net.obvj.smart.conf;

import static net.obvj.smart.TestUtil.assertException;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import javax.xml.bind.UnmarshalException;

import org.junit.Test;
import org.xml.sax.SAXException;

import net.obvj.smart.conf.xml.AgentConfiguration;
import net.obvj.smart.conf.xml.SmartConfiguration;

/**
 * Unit tests for the {@link AgentsXml} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentsXmlTest
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
        SmartConfiguration xml = AgentsXml.loadAgentsXmlFile(XML_TIMER_AGENT_30_SECONDS);
        assertEquals(1, xml.getAgents().size());
        AgentConfiguration agent = xml.getAgents().get(0);
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
        SmartConfiguration xml = AgentsXml.loadAgentsXmlFile(XML_TIMER_AGENT_WITH_DEFAULT_VALUES);
        assertEquals(1, xml.getAgents().size());
        AgentConfiguration agent = xml.getAgents().get(0);
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
                () -> AgentsXml.loadAgentsXmlFile("testAgents/timerAgentWithoutName.xml"));
    }

    @Test
    public void testLoadAgentWithoutType()
    {
        assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
                () -> AgentsXml.loadAgentsXmlFile("testAgents/agentWithoutType.xml"));
    }

    @Test
    public void testLoadTimerAgentWithoutClass()
    {
        assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
                () -> AgentsXml.loadAgentsXmlFile("testAgents/timerAgentWithoutClass.xml"));
    }

    @Test
    public void testLoadTimerAgentWithInvalidName()
    {
        assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
                () -> AgentsXml.loadAgentsXmlFile("testAgents/timerAgentWithInvalidName.xml"));
    }

    @Test
    public void testLoadAgentWithInvalidType()
    {
        assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
                () -> AgentsXml.loadAgentsXmlFile("testAgents/agentWithInvalidType.xml"));
    }

    @Test
    public void testAgentsFileNotFound()
    {
        assertException(AgentConfigurationException.class, null, FileNotFoundException.class,
                () -> AgentsXml.loadAgentsXmlFile("testAgents/notfound.xml"));
    }

    @Test
    public void testLoadXmlWithTwoAgents()
    {
        AgentsXml config = new AgentsXml(XML_TWO_AGENTS);
        assertEquals(2, config.getAgents().size());

        AgentConfiguration dummyAgent = config.getAgentConfiguration(DUMMY_AGENT);
        assertEquals(DUMMY_AGENT, dummyAgent.getName());
        assertEquals(TIMER, dummyAgent.getType());
        assertEquals(DUMMY_AGENT_CLASS, dummyAgent.getAgentClass());
        assertEquals("30 seconds", dummyAgent.getInterval());

        AgentConfiguration dummyDaemon = config.getAgentConfiguration(DUMMY_DAEMON);
        assertEquals(DUMMY_DAEMON, dummyDaemon.getName());
        assertEquals(DAEMON, dummyDaemon.getType());
        assertEquals(DUMMY_DAEMON_CLASS, dummyDaemon.getAgentClass());
    }

    @Test
    public void testLoadSchemaFileWithInvalidFile()
    {
        assertException(AgentConfigurationException.class, null, SAXException.class,
                () -> AgentsXml.loadSchemaFile("testAgents/invalidSchema.xsd"));
    }
}
