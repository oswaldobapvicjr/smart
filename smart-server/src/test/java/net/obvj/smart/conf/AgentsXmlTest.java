package net.obvj.smart.conf;

import static net.obvj.junit.utils.matchers.ExceptionMatcher.throwsException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import javax.xml.bind.UnmarshalException;

import org.junit.Test;
import org.xml.sax.SAXException;

import net.obvj.smart.conf.xml.SmartConfiguration;

/**
 * Unit tests for the {@link AgentsXml} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentsXmlTest
{
    private static final int DEFAULT_STOP_TIMEOUT_SECONDS = Integer.MAX_VALUE;
    // Test files
    private static final String XML_TIMER_AGENT_30_SECONDS = "testAgents/timerAgent30seconds.xml";
    private static final String XML_TIMER_AGENT_30_SECONDS_HIDDEN = "testAgents/timerAgent30secondsHidden.xml";
    private static final String XML_TIMER_AGENT_WITH_DEFAULT_VALUES = "testAgents/timerAgentWithDefaultValues.xml";

    // Test data
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String DUMMY_AGENT_CLASS = "net.obvj.smart.agents.dummy.DummyAgent";
    private static final String TIMER = "timer";

    @Test
    public void testLoadTimerAgent30Seconds()
    {
        SmartConfiguration xml = AgentsXml.loadAgentsXmlFile(XML_TIMER_AGENT_30_SECONDS);
        assertEquals(1, xml.getAgents().size());
        AgentConfiguration agent = xml.getAgents().get(0);
        assertEquals(DUMMY_AGENT, agent.getName());
        assertEquals(DUMMY_AGENT_CLASS, agent.getAgentClass());
        assertEquals(TIMER, agent.getType());
        assertEquals("30 seconds", agent.getFrequency());
        assertEquals(false, agent.isAutomaticallyStarted());
        assertEquals(5, agent.getStopTimeoutInSeconds());
        assertEquals(false, agent.isHidden());
    }

    @Test
    public void testLoadTimerAgent30SecondsHidden()
    {
        SmartConfiguration xml = AgentsXml.loadAgentsXmlFile(XML_TIMER_AGENT_30_SECONDS_HIDDEN);
        assertEquals(1, xml.getAgents().size());
        AgentConfiguration agent = xml.getAgents().get(0);
        assertEquals(DUMMY_AGENT, agent.getName());
        assertEquals(DUMMY_AGENT_CLASS, agent.getAgentClass());
        assertEquals(TIMER, agent.getType());
        assertEquals("30 seconds", agent.getFrequency());
        assertEquals(false, agent.isAutomaticallyStarted());
        assertEquals(5, agent.getStopTimeoutInSeconds());
        assertEquals(true, agent.isHidden());
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
        assertEquals("1", agent.getFrequency());
        assertEquals(true, agent.isAutomaticallyStarted());
        assertEquals(DEFAULT_STOP_TIMEOUT_SECONDS, agent.getStopTimeoutInSeconds());
        assertEquals(false, agent.isHidden());
    }

    @Test
    public void testLoadTimerAgentWithoutName()
    {
        assertThat(() -> AgentsXml.loadAgentsXmlFile("testAgents/timerAgentWithoutName.xml"),
                throwsException(AgentConfigurationException.class).withMessageContaining("Invalid agents file")
                        .withCause(UnmarshalException.class));
    }

    @Test
    public void testLoadAgentWithoutType()
    {
        assertThat(() -> AgentsXml.loadAgentsXmlFile("testAgents/agentWithoutType.xml"),
                throwsException(AgentConfigurationException.class).withMessageContaining("Invalid agents file")
                        .withCause(UnmarshalException.class));
    }

    @Test
    public void testLoadTimerAgentWithoutClass()
    {
        assertThat(() -> AgentsXml.loadAgentsXmlFile("testAgents/timerAgentWithoutClass.xml"),
                throwsException(AgentConfigurationException.class).withMessageContaining("Invalid agents file")
                        .withCause(UnmarshalException.class));
    }

    @Test
    public void testLoadTimerAgentWithInvalidName()
    {
        assertThat(() -> AgentsXml.loadAgentsXmlFile("testAgents/timerAgentWithInvalidName.xml"),
                throwsException(AgentConfigurationException.class).withMessageContaining("Invalid agents file")
                        .withCause(UnmarshalException.class));
    }

    @Test
    public void testLoadAgentWithInvalidType()
    {
        assertThat(() -> AgentsXml.loadAgentsXmlFile("testAgents/agentWithInvalidType.xml"),
                throwsException(AgentConfigurationException.class).withMessageContaining("Invalid agents file")
                        .withCause(UnmarshalException.class));
    }

    @Test
    public void testAgentsFileNotFound()
    {
        assertEquals(Collections.emptyList(), AgentsXml.loadAgentsXmlFile("testAgents/notfound.xml").getAgents());
    }

    @Test
    public void testLoadXmlWithTwoAgents()
    {
        AgentsXml config = new AgentsXml(XML_TIMER_AGENT_30_SECONDS);
        assertEquals(1, config.getAgents().size());

        AgentConfiguration dummyAgent = config.getAgentConfiguration(DUMMY_AGENT);
        assertEquals(DUMMY_AGENT, dummyAgent.getName());
        assertEquals(TIMER, dummyAgent.getType());
        assertEquals(DUMMY_AGENT_CLASS, dummyAgent.getAgentClass());
        assertEquals("30 seconds", dummyAgent.getFrequency());
        assertEquals(false, dummyAgent.isHidden());
    }

    @Test
    public void testLoadSchemaFileWithInvalidFile()
    {
        assertThat(() -> AgentsXml.loadSchemaFile("testAgents/invalidSchema.xsd"),
                throwsException(AgentConfigurationException.class).withCause(SAXException.class));
    }
}
