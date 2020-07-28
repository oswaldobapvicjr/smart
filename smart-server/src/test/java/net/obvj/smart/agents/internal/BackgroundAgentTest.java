package net.obvj.smart.agents.internal;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import net.obvj.smart.agents.Agent;
import net.obvj.smart.agents.AgentFactory;
import net.obvj.smart.agents.impl.AnnotatedTimerAgent;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.conf.annotation.AgentTask;
import net.obvj.smart.util.AnnotationUtils;

/**
 * Unit tests for the {@link BackgroundAgent}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class BackgroundAgentTest
{
    @Mock
    Logger log;

    @InjectMocks
    BackgroundAgent agent;

    @Test
    public void testConfiguration()
    {
        AgentConfiguration config = AgentConfiguration.fromAnnotatedClass(BackgroundAgent.class);
        assertThat(config.getName(), is("BackgroundAgent"));
        assertThat(config.getType(), is("timer"));
        assertThat(config.getFrequency(), is("1"));
        assertThat(config.isAutomaticallyStarted(), is(true));
        assertThat(config.isHidden(), is(true));
    }

    @Test
    public void testAgentIsParsable() throws ReflectiveOperationException
    {
        Agent agent = AgentFactory.create(AgentConfiguration.fromAnnotatedClass(BackgroundAgent.class));
        assertThat(agent, is(instanceOf(AnnotatedTimerAgent.class)));
    }

    @Test
    public void testAgentDefinesARunTask() throws ReflectiveOperationException
    {
        assertThat(AnnotationUtils.getSingleMethodWithAnnotation(BackgroundAgent.class, AgentTask.class),
                is(notNullValue()));
    }

    @Test
    public void testKeepAlive()
    {
        agent.keepAlive();
        verify(log).debug("[BackgroundAgent] Keep alive");
    }

}
