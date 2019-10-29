package net.obvj.smart.agents.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeoutException;

import org.awaitility.Awaitility;
import org.junit.Test;
import org.mockito.Mockito;

import net.obvj.smart.TestUtil;
import net.obvj.smart.agents.api.Agent.State;
import net.obvj.smart.conf.xml.AgentConfiguration;

/**
 * Unit tests for the {@link DaemonAgent} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class DaemonAgentTest
{
    private DaemonAgent agentMock = Mockito.mock(DaemonAgent.class, Mockito.CALLS_REAL_METHODS);

    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder("DummyDaemonAgent")
            .type("daemon").agentClass("net.obvj.smart.agents.dummy.DummyDaemonAgent").stopTimeoutInSeconds(10).build();

    /**
     * Tests that a non-daemon agent will not be parsed by this class
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseNonDaemonAgent() throws ReflectiveOperationException
    {
        DaemonAgent.parseAgent(new AgentConfiguration.Builder("DummyAgent").type("timer")
                .agentClass("net.obvj.smart.agents.dummy.DummyDaemonAgent").build());
    }

    /**
     * Tests that no action is taken when start() is called on a started agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStarted()
    {
        Mockito.when(agentMock.getState()).thenReturn(State.STARTED);
        TestUtil.assertException(IllegalStateException.class, DaemonAgent.MSG_AGENT_ALREADY_STARTED,
                () -> agentMock.start());
    }

    /**
     * Tests that no action is taken when start() is called on a stopped agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStopped()
    {
        Mockito.when(agentMock.getState()).thenReturn(State.STOPPED);
        TestUtil.assertException(IllegalStateException.class, () -> agentMock.start());
    }

    /**
     * Tests agent execution when start() is called on a set agent.
     * 
     * @throws ReflectiveOperationException
     * @throws TimeoutException 
     */
    @Test
    public void testStartAgentWithPreviousStateSet() throws ReflectiveOperationException
    {
        DaemonAgent agent = Mockito.spy((DaemonAgent) Agent.parseAgent(DUMMY_AGENT_CONFIG));
        assertThat("State before start() should be SET", agent.getState(), is(State.SET));
        agent.start();
        Awaitility.await().untilAsserted(() -> Mockito.verify(agent).run());
    }

    /**
     * Tests that no action is taken when stop() is called on a stopped agent.
     */
    @Test
    public void testStopAgentWithPreviousStateStopped()
    {
        Mockito.when(agentMock.isStopped()).thenReturn(true);
        TestUtil.assertException(IllegalStateException.class, DaemonAgent.MSG_AGENT_ALREADY_STOPPED, () ->
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

    @Test
    public void testGetAgentStatusStr() throws ReflectiveOperationException
    {
        DaemonAgent agent = (DaemonAgent) Agent.parseAgent(DUMMY_AGENT_CONFIG);
        String statusWithoutSpaces = agent.getStatusString().replace(" ", "");
        TestUtil.assertStringContains(statusWithoutSpaces, "DummyDaemonAgent", "type:daemon", "status:SET",
                "startDate:null");
        TestUtil.assertStringDoesNotContain(statusWithoutSpaces, "frequency", "lastRun");
    }

}
