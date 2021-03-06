package net.obvj.smart.agents;

import static net.obvj.junit.utils.matchers.ExceptionMatcher.throwsException;
import static net.obvj.junit.utils.matchers.StringMatcher.containsAll;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.mockito.Mockito;

import net.obvj.smart.agents.Agent.State;
import net.obvj.smart.agents.impl.AnnotatedTimerAgent;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.util.TimeInterval;

/**
 * Unit tests for the {@link TimerAgent} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class TimerAgentTest
{
    private TimerAgent agentMock = mock(TimerAgent.class, Mockito.CALLS_REAL_METHODS);

    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder("timer")
            .name("DummyAgent").agentClass("net.obvj.smart.agents.dummy.DummyAgent").frequency("30 seconds")
            .automaticallyStarted(false).stopTimeoutInSeconds(5).build();

    /**
     * Tests that a non-timer agent will not be parsed by this class
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseNonTimerAgent() throws ReflectiveOperationException
    {
        new AnnotatedTimerAgent(new AgentConfiguration.Builder("type_ext").name("DummyAgent")
                .agentClass("net.obvj.smart.agents.dummy.DummyAgent").build());
    }

    /**
     * Tests that no action is taken when start() is called on a started agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStarted()
    {
        when(agentMock.getState()).thenReturn(State.STARTED);
        assertThat(() -> agentMock.start(), throwsException(IllegalStateException.class)
                .withMessageContaining(TimerAgent.MSG_AGENT_ALREADY_STARTED));
    }

    /**
     * Tests that no action is taken when start() is called on a stopped agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStopped()
    {
        when(agentMock.getState()).thenReturn(State.STOPPED);
        assertThat(() -> agentMock.start(), throwsException(IllegalStateException.class));
    }

    /**
     * Tests that no action is taken when stop() is called on a stoppedAgent.
     */
    @Test
    public void testStopAgentWithPreviousStateStopped()
    {
        when(agentMock.isStopped()).thenReturn(true);
        assertThat(() ->
        {
            try
            {
                agentMock.stop();
            }
            catch (TimeoutException e)
            {
                fail("timeout");
            }
        }, throwsException(IllegalStateException.class).withMessageContaining(TimerAgent.MSG_AGENT_ALREADY_STOPPED));
    }

    /**
     * Tests that no action is taken when run() is called on a running agent.
     */
    @Test
    public void testRunAgentWithPreviousStateRunning()
    {
        when(agentMock.isRunning()).thenReturn(true);
        agentMock.run();
        verify(agentMock, never()).runTask();
    }

    /**
     * Tests that exception is thrown when run(true) is called on a running agent.
     */
    @Test(expected = IllegalStateException.class)
    public void testRunManuallyAgentWithPreviousStateRunning()
    {
        when(agentMock.isRunning()).thenReturn(true);
        agentMock.run(true);
    }

    @Test
    public void testRunAgentWithPreviousStateSet() throws ReflectiveOperationException
    {
        TimerAgent agent = spy((TimerAgent) AgentFactory.create(DUMMY_AGENT_CONFIG));
        assertThat("State before run() should be SET", agent.getState(), is(State.SET));
        agent.run();
        verify(agent).runTask();
        assertThat("State after start() should be SET", agent.getState(), is(State.SET));
        assertThat(agent.getLastRunDate(), is(notNullValue()));

    }

    @Test
    public void testGetAgentStatusStr() throws ReflectiveOperationException
    {
        TimerAgent agent = (TimerAgent) AgentFactory.create(DUMMY_AGENT_CONFIG);
        String statusWithoutQuotes = agent.getStatusString().replace("\"", "");
        assertThat(statusWithoutQuotes, containsAll("name:DummyAgent", "type:timer", "status:SET",
                "startDate:null", "lastExecutionStartDate:null", "frequency:30 second(s)", "lastExecutionDuration:null",
                "averageExecutionDuration:0 second(s)"));
    }

    @Test
    public void testGetFrequency()
    {
        TimerAgent agent = (TimerAgent) AgentFactory.create(DUMMY_AGENT_CONFIG);
        assertThat(agent.getFrequency(), is(equalTo(TimeInterval.of("30 seconds"))));
    }

}
