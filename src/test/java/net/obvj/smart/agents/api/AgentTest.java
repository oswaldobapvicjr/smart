package net.obvj.smart.agents.api;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.api.Agent.State;
import net.obvj.smart.agents.dummy.DummyAgent;
import net.obvj.smart.agents.dummy.DummyDaemonAgent;
import net.obvj.smart.conf.xml.AgentConfiguration;
import net.obvj.smart.util.TimeUnit;

/**
 * Unit tests for the {@link Agent} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
public class AgentTest
{
    // Test data
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String DUMMY_AGENT_CLASS = "net.obvj.smart.agents.dummy.DummyAgent";
    private static final String DUMMY_DAEMON = "DummyDaemon";
    private static final String DUMMY_DAEMON_CLASS = "net.obvj.smart.agents.dummy.DummyDaemonAgent";
    private static final String TIMER = "timer";
    private static final String DAEMON = "daemon";

    // The setting CALLS_REAL_METHODS allows mocking abstract methods/classes
    Agent agent = PowerMockito.mock(Agent.class, Mockito.CALLS_REAL_METHODS);
    
    @Test
    public void testParseTimerAgent30Seconds() throws Exception
    {
        AgentConfiguration xmlAgent = new AgentConfiguration.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS)
                .interval("30 seconds")
                .automaticallyStarted(false)
                .stopTimeoutInSeconds(5)
                .build();

        TimerAgent timerAgent = (TimerAgent) Agent.parseAgent(xmlAgent);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(DummyAgent.class)));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(5));
        assertThat(timerAgent.getConfiguration(), is(xmlAgent));
        assertThat(timerAgent.getConfiguration().isHidden(), is(false));

        assertThat(timerAgent.getInterval(), is(30));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.SECONDS));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }
    
    @Test
    public void testParseTimerAgent30SecondsHidden() throws Exception
    {
        AgentConfiguration xmlAgent = new AgentConfiguration.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS)
                .interval("30 seconds")
                .automaticallyStarted(false)
                .stopTimeoutInSeconds(5)
                .hidden(true)
                .build();

        TimerAgent timerAgent = (TimerAgent) Agent.parseAgent(xmlAgent);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(DummyAgent.class)));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(5));
        assertThat(timerAgent.getConfiguration(), is(xmlAgent));
        assertThat(timerAgent.getConfiguration().isHidden(), is(true));

        assertThat(timerAgent.getInterval(), is(30));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.SECONDS));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }
    
    @Test
    public void testParseTimerAgentDefaultValues() throws Exception
    {
        AgentConfiguration xmlAgent = new AgentConfiguration.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS)
                .build();

        TimerAgent timerAgent = (TimerAgent) Agent.parseAgent(xmlAgent);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(DummyAgent.class)));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(-1));
        assertThat(timerAgent.getConfiguration(), is(xmlAgent));
        assertThat(timerAgent.getConfiguration().isHidden(), is(false));

        assertThat(timerAgent.getInterval(), is(1));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.MINUTES));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }
    
    @Test
    public void testParseDaemonAgent() throws Exception
    {
        AgentConfiguration xmlAgent = new AgentConfiguration.Builder(DUMMY_DAEMON)
                .type(DAEMON)
                .agentClass(DUMMY_DAEMON_CLASS)
                .automaticallyStarted(true)
                .stopTimeoutInSeconds(1)
                .build();

        DaemonAgent daemonAgent = (DaemonAgent) Agent.parseAgent(xmlAgent);

        assertThat(daemonAgent.getName(), is(DUMMY_DAEMON));
        assertThat(daemonAgent.getType(), is(DAEMON));
        assertThat(daemonAgent.getClass(), is(equalTo(DummyDaemonAgent.class)));
        assertThat(daemonAgent.getStopTimeoutSeconds(), is(1));
        assertThat(daemonAgent.getConfiguration(), is(xmlAgent));
        assertThat(daemonAgent.getConfiguration().isHidden(), is(false));

        assertThat(daemonAgent.getState(), is(State.SET));
        assertThat(daemonAgent.isStarted(), is(false));
    }
    
    @Test
    public void testParseDaemonAgentDefaultValues() throws Exception
    {
        AgentConfiguration xmlAgent = new AgentConfiguration.Builder(DUMMY_DAEMON)
                .type(DAEMON)
                .agentClass(DUMMY_DAEMON_CLASS)
                .build();

        DaemonAgent daemonAgent = (DaemonAgent) Agent.parseAgent(xmlAgent);
        
        assertThat(daemonAgent.getName(), is(DUMMY_DAEMON));
        assertThat(daemonAgent.getType(), is(DAEMON));
        assertThat(daemonAgent.getClass(), is(equalTo(DummyDaemonAgent.class)));
        assertThat(daemonAgent.getStopTimeoutSeconds(), is(-1));
        assertThat(daemonAgent.getConfiguration(), is(xmlAgent));
        assertThat(daemonAgent.getConfiguration().isHidden(), is(false));

        assertThat(daemonAgent.getState(), is(State.SET));
        assertThat(daemonAgent.isStarted(), is(false));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testParseUnknownAgentType() throws Exception
    {
        AgentConfiguration xmlAgent = new AgentConfiguration.Builder(DUMMY_DAEMON)
                .type("unknown")
                .agentClass(DUMMY_DAEMON_CLASS)
                .build();

        Agent.parseAgent(xmlAgent);
    }
    
    @Test
    public void testStatusMethodsForAgentStatusSet()
    {
        agent.setState(State.SET);
        assertFalse("expected false on agent.isStarted()", agent.isStarted());
        assertFalse("expected false on agent.isRunning()", agent.isRunning());
        assertFalse("expected false on agent.isStopped()", agent.isStopped());
    }
    
    @Test
    public void testStatusMethodsForAgentStatusStarted()
    {
        agent.setState(State.STARTED);
        assertTrue("expected true on agent.isStarted()", agent.isStarted());
        assertFalse("expected false on agent.isRunning()", agent.isRunning());
        assertFalse("expected false on agent.isStopped()", agent.isStopped());
    }
    
    @Test
    public void testStatusMethodsForAgentStatusRunning()
    {
        agent.setState(State.RUNNING);
        assertFalse("expected false on agent.isStarted()", agent.isStarted());
        assertTrue("expected true on agent.isRunning()", agent.isRunning());
        assertFalse("expected false on agent.isStopped()", agent.isStopped());
    }
    
    @Test
    public void testStatusMethodsForAgentStatusStopped()
    {
        agent.setState(State.STOPPED);
        assertFalse("expected false on agent.isStarted()", agent.isStarted());
        assertFalse("expected false on agent.isRunning()", agent.isRunning());
        assertTrue("expected true on agent.isStopped()", agent.isStopped());
    }
    
    @Test
    public void testLastRunDate()
    {
        Calendar now = Calendar.getInstance();
        agent.lastRunDate = now;
        Calendar lastRunDate = agent.getLastRunDate();
        assertNotSame(now, lastRunDate);
        assertThat(lastRunDate.getTime(), is(now.getTime()));
    }
    
    @Test
    public void testLastRunDateNull()
    {
        agent.lastRunDate = null;
        Calendar lastRunDate = agent.getLastRunDate();
        assertNull(lastRunDate);
    }

    @Test
    public void testIsAutomaticallyStarted() throws Exception
    {
        AgentConfiguration xmlAgent = new AgentConfiguration.Builder(DUMMY_DAEMON)
                .type(DAEMON)
                .agentClass(DUMMY_DAEMON_CLASS)
                .automaticallyStarted(true)
                .build();

        DaemonAgent daemonAgent = (DaemonAgent) Agent.parseAgent(xmlAgent);
        assertTrue(daemonAgent.isAutomaticallyStarted());
    }
    
}
