package net.obvj.smart.agents.internal;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.agents.impl.AnnotatedTimerAgent;
import net.obvj.smart.conf.AgentConfiguration;

/**
 * Unit tests for the {@link BackgroundAgent}.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class BackgroundAgentTest
{
    @Test
    public void testConfiguration()
    {
        AgentConfiguration config = AgentConfiguration.fromAnnotatedClass(BackgroundAgent.class);
        assertThat(config.getName(), is("BackgroundAgent"));
        assertThat(config.getType(), is("timer"));
        assertThat(config.getInterval(), is("1"));
        assertThat(config.isAutomaticallyStarted(), is(true));
        assertThat(config.isHidden(), is(true));
    }
    
    @Test
    public void testAgentIsParsable() throws ReflectiveOperationException
    {
        Agent agent = Agent.parseAgent(AgentConfiguration.fromAnnotatedClass(BackgroundAgent.class));
        assertThat(agent, is(instanceOf(AnnotatedTimerAgent.class)));
    }

}
