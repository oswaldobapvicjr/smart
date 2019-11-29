package net.obvj.smart.conf;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import net.obvj.smart.agents.test.invalid.TestAgentWithAllCustomParams;
import net.obvj.smart.agents.test.invalid.TestAgentWithCustomNameAndType;
import net.obvj.smart.agents.test.invalid.TestAgentWithNoType;
import net.obvj.smart.agents.test.invalid.TestAgentWithTypeDeterminedBySupertypeDaemon;
import net.obvj.smart.agents.test.valid.TestAgentWithNoNameAndTypeTimerAndAgentTask;
import net.obvj.smart.agents.test.valid.TestAgentWithTypeDeterminedBySupertypeTimer;

/**
 * Unit tests for the {@link AgentConfiguration}.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentConfigurationTest
{
    private static final String NAME1 = "name1";
    private static final String DAEMON = "daemon";
    private static final String TIMER = "timer";
    private static final String INTERVAL = "90 seconds";
    private static final int STOP_TIMEOUT_SECONDS = 99;

    @Test(expected = AgentConfigurationException.class)
    public void fromAnnotatedClass_withAgentAnnotationNotPresent()
    {
        AgentConfiguration.fromAnnotatedClass(Boolean.class);
    }

    @Test(expected = AgentConfigurationException.class)
    public void fromAnnotatedClass_withAgentAnnotationWithNoType()
    {
        AgentConfiguration.fromAnnotatedClass(TestAgentWithNoType.class);
    }

    @Test()
    public void fromAnnotatedClass_withAgentAnnotationPresentAndCustomTypeTimer()
    {
        AgentConfiguration configuration = AgentConfiguration.fromAnnotatedClass(TestAgentWithNoNameAndTypeTimerAndAgentTask.class);

        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.getName(), is(TestAgentWithNoNameAndTypeTimerAndAgentTask.class.getSimpleName()));
        assertThat(configuration.getType(), is(TIMER));
        assertThat(configuration.getAgentClass(), is(TestAgentWithNoNameAndTypeTimerAndAgentTask.class.getCanonicalName()));
        assertThat(configuration.getInterval(), is(AgentConfiguration.DEFAULT_INTERVAL));
        assertThat(configuration.getStopTimeoutInSeconds(), is(AgentConfiguration.DEFAULT_STOP_TIMEOUT_IN_SECONDS));
        assertThat(configuration.isAutomaticallyStarted(), is(AgentConfiguration.DEFAULT_AUTOMATICALLY_STARTED));
        assertThat(configuration.isHidden(), is(AgentConfiguration.DEFAULT_HIDDEN));
    }

    @Test()
    public void fromAnnotatedClass_withAgentAnnotationPresentAndCustomNameAndType()
    {
        AgentConfiguration configuration = AgentConfiguration.fromAnnotatedClass(TestAgentWithCustomNameAndType.class);

        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.getName(), is(NAME1));
        assertThat(configuration.getType(), is(TIMER));
        assertThat(configuration.getAgentClass(), is(TestAgentWithCustomNameAndType.class.getCanonicalName()));
        assertThat(configuration.getInterval(), is(AgentConfiguration.DEFAULT_INTERVAL));
        assertThat(configuration.getStopTimeoutInSeconds(), is(AgentConfiguration.DEFAULT_STOP_TIMEOUT_IN_SECONDS));
        assertThat(configuration.isAutomaticallyStarted(), is(AgentConfiguration.DEFAULT_AUTOMATICALLY_STARTED));
        assertThat(configuration.isHidden(), is(AgentConfiguration.DEFAULT_HIDDEN));
    }

    @Test()
    public void fromAnnotatedClass_withAgentAnnotationAndTypeDeterminedBySupertypeTimer()
    {
        Class<?> clazz = TestAgentWithTypeDeterminedBySupertypeTimer.class;
        AgentConfiguration configuration = AgentConfiguration.fromAnnotatedClass(clazz);

        // Main subject
        assertThat(configuration.getType(), is(TIMER));

        // Default values
        assertThat(configuration.getName(), is(clazz.getSimpleName()));
        assertThat(configuration.getAgentClass(), is(clazz.getCanonicalName()));
        assertThat(configuration.getInterval(), is(AgentConfiguration.DEFAULT_INTERVAL));
        assertThat(configuration.getStopTimeoutInSeconds(), is(AgentConfiguration.DEFAULT_STOP_TIMEOUT_IN_SECONDS));
        assertThat(configuration.isAutomaticallyStarted(), is(AgentConfiguration.DEFAULT_AUTOMATICALLY_STARTED));
        assertThat(configuration.isHidden(), is(AgentConfiguration.DEFAULT_HIDDEN));
    }

    @Test()
    public void fromAnnotatedClass_withAgentAnnotationAndTypeDeterminedBySupertypeDaemon()
    {
        Class<?> clazz = TestAgentWithTypeDeterminedBySupertypeDaemon.class;
        AgentConfiguration configuration = AgentConfiguration.fromAnnotatedClass(clazz);

        // Main subject
        assertThat(configuration.getType(), is(DAEMON));

        // Default values
        assertThat(configuration.getName(), is(clazz.getSimpleName()));
        assertThat(configuration.getAgentClass(), is(clazz.getCanonicalName()));
        assertThat(configuration.getInterval(), is(AgentConfiguration.DEFAULT_INTERVAL));
        assertThat(configuration.getStopTimeoutInSeconds(), is(AgentConfiguration.DEFAULT_STOP_TIMEOUT_IN_SECONDS));
        assertThat(configuration.isAutomaticallyStarted(), is(AgentConfiguration.DEFAULT_AUTOMATICALLY_STARTED));
        assertThat(configuration.isHidden(), is(AgentConfiguration.DEFAULT_HIDDEN));
    }

    @Test()
    public void fromAnnotatedClass_withAgentAnnotationAndAllCustomParams()
    {
        Class<?> clazz = TestAgentWithAllCustomParams.class;
        AgentConfiguration configuration = AgentConfiguration.fromAnnotatedClass(clazz);

        assertThat(configuration.getName(), is(NAME1));
        assertThat(configuration.getType(), is(TIMER));
        assertThat(configuration.getInterval(), is(INTERVAL));
        assertThat(configuration.getAgentClass(), is(clazz.getCanonicalName()));
        assertThat(configuration.getStopTimeoutInSeconds(), is(STOP_TIMEOUT_SECONDS));
        assertThat(configuration.isAutomaticallyStarted(), is(false));
        assertThat(configuration.isHidden(), is(true));
    }

}
