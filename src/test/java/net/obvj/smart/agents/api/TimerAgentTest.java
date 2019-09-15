package net.obvj.smart.agents.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.mockito.Mockito;

import net.obvj.smart.TestUtil;
import net.obvj.smart.agents.api.Agent.State;
import net.obvj.smart.conf.xml.AgentConfiguration;

/**
 * Unit tests for the {@link TimerAgent} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class TimerAgentTest
{
    private TimerAgent agentMock = mock(TimerAgent.class, Mockito.CALLS_REAL_METHODS);

    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder("DummyAgent")
            .type("timer").agentClass("net.obvj.smart.agents.dummy.DummyAgent").interval("30 seconds")
            .automaticallyStarted(false).stopTimeoutInSeconds(5).build();

    /**
     * Tests that a non-timer agent will not be parsed by this class
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseNonTimerAgent() throws ReflectiveOperationException
    {
        TimerAgent.parseAgent(new AgentConfiguration.Builder("DummyDaemonAgent").type("daemon")
                .agentClass("net.obvj.smart.agents.dummy.DummyDaemonAgent").build());
    }

    /**
     * Tests that no action is taken when start() is called on a started agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStarted()
    {
        when(agentMock.getState()).thenReturn(State.STARTED);
        TestUtil.assertException(IllegalStateException.class, TimerAgent.MSG_AGENT_ALREADY_STARTED,
                () -> agentMock.start());
    }

    /**
     * Tests that no action is taken when start() is called on a stopped agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStopped()
    {
        when(agentMock.getState()).thenReturn(State.STOPPED);
        TestUtil.assertException(IllegalStateException.class, () -> agentMock.start());
    }

    /**
     * Tests that no action is taken when stop() is called on a stoppedAgent.
     */
    @Test
    public void testStopAgentWithPreviousStateStopped()
    {
        when(agentMock.isStopped()).thenReturn(true);
        TestUtil.assertException(IllegalStateException.class, TimerAgent.MSG_AGENT_ALREADY_STOPPED, () ->
        {
            try
            {
                agentMock.stop();
            }
            catch (TimeoutException e)
            {
                fail("timeout");
            }
        });
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

    @Test
    public void testRunAgentWithPreviousStateSet() throws ReflectiveOperationException
    {
        TimerAgent agent = spy((TimerAgent) Agent.parseAgent(DUMMY_AGENT_CONFIG));
        assertEquals("State before run()", State.SET, agent.getState());
        agent.run();
        verify(agent).runTask();
        assertEquals("State after run()", State.SET, agent.getState());
        assertNotNull(agent.getLastRunDate());
        
    }

    @Test
    public void testGetAgentStatusStr() throws ReflectiveOperationException
    {
        TimerAgent agent = (TimerAgent) Agent.parseAgent(DUMMY_AGENT_CONFIG);
        String statusWithoutSpaces = agent.getStatusString().replace(" ", "");
        TestUtil.assertStringContains(statusWithoutSpaces, "DummyAgent", "type:timer", "status:SET", "startDate:null",
                "lastRun:null", "frequency:30second(s)");
    }

}
