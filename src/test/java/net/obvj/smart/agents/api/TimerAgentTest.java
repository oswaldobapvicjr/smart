package net.obvj.smart.agents.api;

import static org.junit.Assert.fail;

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
    private TimerAgent agent = Mockito.mock(TimerAgent.class);

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
        Mockito.when(agent.getState()).thenReturn(State.STARTED);
        TestUtil.assertException(IllegalStateException.class, TimerAgent.MSG_AGENT_ALREADY_STARTED,
                () -> agent.start());
    }

    /**
     * Tests that no action is taken when start() is called on a stopped agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStopped()
    {
        Mockito.when(agent.getState()).thenReturn(State.STOPPED);
        TestUtil.assertException(IllegalStateException.class, () -> agent.start());
    }

    /**
     * Tests that no action is taken when stop() is called on a stoppedAgent.
     */
    @Test
    public void testStopAgentWithPreviousStateStopped()
    {
        Mockito.when(agent.isStopped()).thenReturn(true);
        TestUtil.assertException(IllegalStateException.class, TimerAgent.MSG_AGENT_ALREADY_STOPPED, () ->
        {
            try
            {
                agent.stop();
            }
            catch (TimeoutException e)
            {
                fail("timeout");
            }
        });
    }

}
