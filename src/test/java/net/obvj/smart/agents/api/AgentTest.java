package net.obvj.smart.agents.api;

import static org.junit.Assert.*;

import org.junit.Test;

import net.obvj.smart.agents.api.Agent.State;
import net.obvj.smart.agents.dummy.DummyAgent;
import net.obvj.smart.conf.xml.XmlAgent;
import net.obvj.smart.util.DateUtil.TimeUnit;

/**
 * Unit tests for the {@link Agent} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentTest
{
    // Test data
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String DUMMY_AGENT_CLASS = "net.obvj.smart.agents.dummy.DummyAgent";
    private static final String TIMER = "timer";

    @Test
    public void testParseTimerAgent30Seconds() throws Exception
    {
        XmlAgent xmlAgent = new XmlAgent.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS)
                .interval("30 seconds")
                .automaticallyStarted(false)
                .stopTimeoutInSeconds(5).build();

        TimerAgent timerAgent = (TimerAgent) Agent.parseAgent(xmlAgent);
        
        assertEquals(DUMMY_AGENT, timerAgent.getName());
        assertEquals(TIMER, timerAgent.getType());
        assertEquals(DummyAgent.class, timerAgent.getClass());
        assertEquals(5, timerAgent.getStopTimeoutSeconds());
        assertEquals(State.SET, timerAgent.getState());

        assertEquals(30, timerAgent.getInterval());
        assertEquals(TimeUnit.SECONDS, timerAgent.getTimeUnit());
    }

}
