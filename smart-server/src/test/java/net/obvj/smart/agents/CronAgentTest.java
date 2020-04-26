package net.obvj.smart.agents;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import net.obvj.junit.utils.TestUtils;
import net.obvj.smart.agents.Agent.State;
import net.obvj.smart.agents.impl.AnnotatedCronAgent;
import net.obvj.smart.conf.AgentConfiguration;

/**
 * Unit tests for the {@link CronAgent} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class CronAgentTest
{
    private CronAgent agentMock = mock(CronAgent.class, Mockito.CALLS_REAL_METHODS);

    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder("cron")
            .name("DummyAgent")
            .agentClass("net.obvj.smart.agents.test.valid.TestAgentWithNoNameAndTypeCronAndAgentTask")
            .frequency("0 0 * * 0").automaticallyStarted(false).stopTimeoutInSeconds(5).build();

    @Before
    public void setup()
    {
        Locale.setDefault(Locale.UK);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Tests that a non-cron agent will not be parsed by this class
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseNonCronAgent() throws ReflectiveOperationException
    {
        new AnnotatedCronAgent(new AgentConfiguration.Builder("timer").name("DummyAgent")
                .agentClass("net.obvj.smart.agents.dummy.DummyAgent").build());
    }

    /**
     * Tests that no action is taken when start() is called on a started agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStarted()
    {
        when(agentMock.getState()).thenReturn(State.STARTED);
        TestUtils.assertException(IllegalStateException.class, CronAgent.MSG_AGENT_ALREADY_STARTED,
                () -> agentMock.start());
    }

    /**
     * Tests that no action is taken when start() is called on a stopped agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStopped()
    {
        when(agentMock.getState()).thenReturn(State.STOPPED);
        TestUtils.assertException(IllegalStateException.class, () -> agentMock.start());
    }

    /**
     * Tests that no action is taken when stop() is called on a stopped agent.
     */
    @Test
    public void testStopAgentWithPreviousStateStopped()
    {
        when(agentMock.isStopped()).thenReturn(true);
        TestUtils.assertException(IllegalStateException.class, CronAgent.MSG_AGENT_ALREADY_STOPPED, () ->
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
        verify(agentMock, never()).doRunTask();
    }

    /**
     * Tests that an exception is thrown when run(true) is called on a running agent.
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
        CronAgent agent = spy((CronAgent) AgentFactory.create(DUMMY_AGENT_CONFIG));
        assertThat("State before run() should be SET", agent.getState(), is(State.SET));
        agent.run();
        verify(agent).runTask();
        assertThat("State after start() should be SET", agent.getState(), is(State.SET));
        assertThat(agent.getLastRunDate(), is(notNullValue()));
    }

    @Test
    public void testGetAgentStatusStr() throws ReflectiveOperationException
    {
        CronAgent agent = (CronAgent) AgentFactory.create(DUMMY_AGENT_CONFIG);
        String statusWithoutQuotes = agent.getStatusString().replace("\"", "");
        TestUtils.assertStringContains(statusWithoutQuotes, "name:DummyAgent", "type:cron", "status:SET",
                "startDate:null", "lastRunDate:null", "cronExpression:0 0 * * 0", "cronDescription");
    }

    @Test
    public void testGetCronExpression()
    {
        CronAgent agent = (CronAgent) AgentFactory.create(DUMMY_AGENT_CONFIG);
        assertThat(agent.getCronExpression(), is(equalTo("0 0 * * 0")));
    }

}
