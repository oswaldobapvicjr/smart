package net.obvj.smart.agents;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.obvj.junit.utils.TestUtils;
import net.obvj.smart.agents.Agent.State;
import net.obvj.smart.agents.dummy.DummyAgent;
import net.obvj.smart.agents.impl.AnnotatedTimerAgent;
import net.obvj.smart.agents.test.valid.TestAgentWithNoNameAndTypeTimerAndAgentTask;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.util.TimeUnit;

/**
 * Unit tests for the {@link AgentFactory}.
 *
 * @author oswado.bapvic.jr
 * @since 2.0
 */
public class AgentFactoryTest
{
    // Test data
    private static final int DEFAULT_STOP_TIMEOUT_SECONDS = Integer.MAX_VALUE;
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String DUMMY_AGENT_CLASS = "net.obvj.smart.agents.dummy.DummyAgent";
    private static final String TIMER = "timer";

    @Test
    public void constuctor_notAllowed() throws ReflectiveOperationException
    {
        TestUtils.assertNoInstancesAllowed(AgentFactory.class, IllegalStateException.class);
    }

    @Test
    public void create_timerAgent30Seconds() throws Exception
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT).type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS).frequency("30 seconds").automaticallyStarted(false)
                .stopTimeoutInSeconds(5).build();

        TimerAgent timerAgent = (TimerAgent) AgentFactory.create(configuration);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(AnnotatedTimerAgent.class)));
        assertThat(timerAgent.getConfiguration().getAgentClass(), is(DummyAgent.class.getName()));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(5));
        assertThat(timerAgent.getConfiguration(), is(configuration));
        assertThat(timerAgent.isHidden(), is(false));

        assertThat(timerAgent.getIntervalDuration(), is(30));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.SECONDS));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }

    @Test
    public void create_timerAgent30SecondsHidden() throws Exception
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT).type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS).frequency("30 seconds").automaticallyStarted(false)
                .stopTimeoutInSeconds(5).hidden(true).build();

        TimerAgent timerAgent = (TimerAgent) AgentFactory.create(configuration);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(AnnotatedTimerAgent.class)));
        assertThat(timerAgent.getConfiguration().getAgentClass(), is(DummyAgent.class.getName()));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(5));
        assertThat(timerAgent.getConfiguration(), is(configuration));
        assertThat(timerAgent.isHidden(), is(true));

        assertThat(timerAgent.getIntervalDuration(), is(30));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.SECONDS));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }

    @Test
    public void create_timerAgentDefaultValues() throws Exception
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT).type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS).build();

        TimerAgent timerAgent = (TimerAgent) AgentFactory.create(configuration);

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getClass(), is(equalTo(AnnotatedTimerAgent.class)));
        assertThat(timerAgent.getConfiguration().getAgentClass(), is(DummyAgent.class.getName()));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(DEFAULT_STOP_TIMEOUT_SECONDS));
        assertThat(timerAgent.getConfiguration(), is(configuration));
        assertThat(timerAgent.isHidden(), is(false));

        assertThat(timerAgent.getIntervalDuration(), is(1));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.MINUTES));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_UnknownAgentType_fails() throws Exception
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT).type("unknown")
                .agentClass(DUMMY_AGENT_CLASS).build();

        AgentFactory.create(configuration);
    }

    @Test
    public void create_annotatedTimerAgent() throws Exception
    {
        Class<TestAgentWithNoNameAndTypeTimerAndAgentTask> testClass = TestAgentWithNoNameAndTypeTimerAndAgentTask.class;

        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT).type(TIMER)
                .agentClass(testClass.getName()).frequency("30 seconds").automaticallyStarted(false)
                .stopTimeoutInSeconds(5).hidden(true).build();

        TimerAgent timerAgent = (TimerAgent) AgentFactory.create(configuration);
        assertThat(timerAgent, instanceOf(AnnotatedTimerAgent.class));

        assertThat(timerAgent.getName(), is(DUMMY_AGENT));
        assertThat(timerAgent.getType(), is(TIMER));
        assertThat(timerAgent.getStopTimeoutSeconds(), is(5));
        assertThat(timerAgent.getConfiguration(), is(configuration));
        assertThat(timerAgent.isHidden(), is(true));

        assertThat(timerAgent.getIntervalDuration(), is(30));
        assertThat(timerAgent.getTimeUnit(), is(TimeUnit.SECONDS));

        assertThat(timerAgent.getState(), is(State.SET));
        assertThat(timerAgent.isStarted(), is(false));
    }

    @Test
    public void create_automaticallyStartedAgent() throws Exception
    {
        AgentConfiguration configuration = new AgentConfiguration.Builder(DUMMY_AGENT).type(TIMER)
                .agentClass(DUMMY_AGENT_CLASS).automaticallyStarted(true).build();

        TimerAgent agent = (TimerAgent) AgentFactory.create(configuration);
        assertTrue(agent.isAutomaticallyStarted());
    }

}
