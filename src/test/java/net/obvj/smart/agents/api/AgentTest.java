package net.obvj.smart.agents.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.api.Agent.State;
import net.obvj.smart.agents.dummy.DummyAgent;
import net.obvj.smart.agents.impl.AnnotatedTimerAgent;
import net.obvj.smart.agents.test.valid.TestAgentWithNoNameAndTypeTimerAndAgentTask;
import net.obvj.smart.conf.AgentConfiguration;
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
    private static final int DEFAULT_STOP_TIMEOUT_SECONDS = Integer.MAX_VALUE;
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String DUMMY_AGENT_CLASS = "net.obvj.smart.agents.dummy.DummyAgent";
    private static final String TIMER = "timer";

    // The setting CALLS_REAL_METHODS allows mocking abstract methods/classes
    Agent agent = PowerMockito.mock(Agent.class, Mockito.CALLS_REAL_METHODS);

    @Test
    public void testParseTimerAgent30Seconds() throws Exception
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS)
                .interval("30 seconds")
                .automaticallyStarted(false)
                .stopTimeoutInSeconds(5)
                .build();

        TimerAgent timerAgent = (TimerAgent) Agent.parseAgent(configuration);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(DummyAgent.class)));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(5));
        assertThat(timerAgent.getConfiguration(), is(configuration));
        assertThat(timerAgent.isHidden(), is(false));

        assertThat(timerAgent.getInterval(), is(30));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.SECONDS));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }

    @Test
    public void testParseTimerAgent30SecondsHidden() throws Exception
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS)
                .interval("30 seconds")
                .automaticallyStarted(false)
                .stopTimeoutInSeconds(5)
                .hidden(true)
                .build();

        TimerAgent timerAgent = (TimerAgent) Agent.parseAgent(configuration);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(DummyAgent.class)));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(5));
        assertThat(timerAgent.getConfiguration(), is(configuration));
        assertThat(timerAgent.isHidden(), is(true));

        assertThat(timerAgent.getInterval(), is(30));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.SECONDS));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }

    @Test
    public void testParseTimerAgentDefaultValues() throws Exception
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS)
                .build();

        TimerAgent timerAgent = (TimerAgent) Agent.parseAgent(configuration);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(DummyAgent.class)));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(DEFAULT_STOP_TIMEOUT_SECONDS));
        assertThat(timerAgent.getConfiguration(), is(configuration));
        assertThat(timerAgent.isHidden(), is(false));

        assertThat(timerAgent.getInterval(), is(1));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.MINUTES));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseUnknownAgentType() throws Exception
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT)
                .type("unknown")
                .agentClass(DUMMY_AGENT_CLASS)
                .build();

        Agent.parseAgent(configuration);
    }

    @Test
    public void testParseAnnotatedTimerAgent() throws Exception
    {
        Class<TestAgentWithNoNameAndTypeTimerAndAgentTask> testClass = TestAgentWithNoNameAndTypeTimerAndAgentTask.class;

        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(testClass.getName())
                .interval("30 seconds")
                .automaticallyStarted(false)
                .stopTimeoutInSeconds(5)
                .hidden(true)
                .build();

        TimerAgent timerAgent = (TimerAgent) Agent.parseAgent(configuration);
        assertThat(timerAgent, instanceOf(AnnotatedTimerAgent.class));

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(5));
        assertThat(timerAgent.getConfiguration(), is(configuration));
        assertThat(timerAgent.isHidden(), is(true));

        assertThat(timerAgent.getInterval(), is(30));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.SECONDS));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
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
        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT)
                .type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS)
                .automaticallyStarted(true)
                .build();

        TimerAgent agent = (TimerAgent) Agent.parseAgent(configuration);
        assertTrue(agent.isAutomaticallyStarted());
    }

}
