package net.obvj.smart.agents.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import net.obvj.smart.agents.api.Agent.State;
import net.obvj.smart.agents.dummy.DummyAgent;
import net.obvj.smart.agents.dummy.DummyDaemonAgent;
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
    private static final String DUMMY_DAEMON = "DummyDaemon";
    private static final String DUMMY_DAEMON_CLASS = "net.obvj.smart.agents.dummy.DummyDaemonAgent";
    private static final String TIMER = "timer";
    private static final String DAEMON = "daemon";

    @Test
    public void testParseTimerAgent30Seconds() throws Exception
    {
        XmlAgent xmlAgent = new XmlAgent.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS)
                .interval("30 seconds")
                .automaticallyStarted(false)
                .stopTimeoutInSeconds(5)
                .build();

        TimerAgent timerAgent = (TimerAgent) Agent.parseAgent(xmlAgent);
        
        assertEquals(DUMMY_AGENT, timerAgent.getName());
        assertEquals(TIMER, timerAgent.getType());
        assertEquals(DummyAgent.class, timerAgent.getClass());
        assertEquals(5, timerAgent.getStopTimeoutSeconds());
        
        assertEquals(30, timerAgent.getInterval());
        assertEquals(TimeUnit.SECONDS, timerAgent.getTimeUnit());
        
        assertEquals(State.SET, timerAgent.getState());
        assertFalse(timerAgent.isStarted());
    }
    
    @Test
    public void testParseTimerAgentDefaultValues() throws Exception
    {
        XmlAgent xmlAgent = new XmlAgent.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS)
                .build();

        TimerAgent timerAgent = (TimerAgent) Agent.parseAgent(xmlAgent);
        
        assertEquals(DUMMY_AGENT, timerAgent.getName());
        assertEquals(TIMER, timerAgent.getType());
        assertEquals(DummyAgent.class, timerAgent.getClass());
        assertEquals(-1, timerAgent.getStopTimeoutSeconds());
        
        assertEquals(1, timerAgent.getInterval());
        assertEquals(TimeUnit.MINUTES, timerAgent.getTimeUnit());
        
        assertEquals(State.SET, timerAgent.getState());
        assertFalse(timerAgent.isStarted());
    }
    
    @Test
    public void testParseDaemonAgent() throws Exception
    {
        XmlAgent xmlAgent = new XmlAgent.Builder(DUMMY_DAEMON)
                .type(DAEMON)
                .agentClass(DUMMY_DAEMON_CLASS)
                .automaticallyStarted(true)
                .stopTimeoutInSeconds(1)
                .build();

        DaemonAgent daemonAgent = (DaemonAgent) Agent.parseAgent(xmlAgent);
        
        assertEquals(DUMMY_DAEMON, daemonAgent.getName());
        assertEquals(DAEMON, daemonAgent.getType());
        assertEquals(DummyDaemonAgent.class, daemonAgent.getClass());
        assertEquals(1, daemonAgent.getStopTimeoutSeconds());
        
        assertEquals(State.SET, daemonAgent.getState());
        assertFalse(daemonAgent.isStarted());
    }
    
    @Test
    public void testParseDaemonAgentDefaultValues() throws Exception
    {
        XmlAgent xmlAgent = new XmlAgent.Builder(DUMMY_DAEMON)
                .type(DAEMON)
                .agentClass(DUMMY_DAEMON_CLASS)
                .build();

        DaemonAgent daemonAgent = (DaemonAgent) Agent.parseAgent(xmlAgent);
        
        assertEquals(DUMMY_DAEMON, daemonAgent.getName());
        assertEquals(DAEMON, daemonAgent.getType());
        assertEquals(DummyDaemonAgent.class, daemonAgent.getClass());
        assertEquals(-1, daemonAgent.getStopTimeoutSeconds());
        
        assertEquals(State.SET, daemonAgent.getState());
        assertFalse(daemonAgent.isStarted());
    }

}
