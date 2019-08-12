package net.obvj.smart.conf;

import static org.junit.Assert.assertEquals;

import java.util.function.Supplier;

import javax.xml.bind.UnmarshalException;

import org.junit.Test;

import net.obvj.smart.conf.xml.XmlAgent;
import net.obvj.smart.conf.xml.XmlSmart;

public class AgentConfigurationTest
{
    // Test files
    private static final String XML_TIMER_AGENT_30_SECONDS = "testAgents/timerAgent30seconds.xml";
    private static final String XML_TIMER_AGENT_WITH_DEFAULT_VALUES = "testAgents/timerAgentWithDefaultValues.xml";
    private static final String XML_TIMER_AGENT_WITHOUT_NAME = "testAgents/timerAgentWithoutName.xml";

    // Test data
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String DUMMY_AGENT_CLASS = "net.obvj.smart.agents.dummy.DummyAgent";
    private static final String TIMER = "timer";

    /**
     * A utility method to assert the expected throwable and cause classes thrown by a
     * supplying function.
     * 
     * @param expectedThrowable the expected throwable class
     * @param expectedCause     the expected throwable cause class (if applicable)
     * @param expectedMessage   the expected message (if applicable)
     * @param supplier          the supplying function that produces an exception to be
     *                          validated
     */
    public void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Class<? extends Throwable> expectedCause, Supplier<XmlSmart> supplier)
    {
        try
        {
            supplier.get();
        }
        catch (Throwable throwable)
        {
            assertEquals(expectedThrowable, throwable.getClass());
            if (expectedMessage != null) assertEquals(expectedMessage, throwable.getMessage());
            if (expectedCause != null) assertEquals(expectedCause, throwable.getCause().getClass());
        }
    }

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
                () -> AgentConfiguration.loadAgentsXmlFile(XML_TIMER_AGENT_WITHOUT_NAME));
    }

}
