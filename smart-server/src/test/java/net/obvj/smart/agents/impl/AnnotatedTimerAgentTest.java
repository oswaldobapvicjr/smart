package net.obvj.smart.agents.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.junit.utils.TestUtils;
import net.obvj.smart.agents.test.invalid.TestAgentWithAllCustomParamsAndPrivateAgentTask;
import net.obvj.smart.agents.test.invalid.TestAgentWithAllCustomParamsAndPrivateConstructor;
import net.obvj.smart.agents.test.invalid.TestAgentWithNoNameAndTypeTimerAndNoAgentTask;
import net.obvj.smart.agents.test.invalid.TestAgentWithNoNameAndTypeTimerAndTwoAgentTasks;
import net.obvj.smart.agents.test.valid.TestAgentWithNoNameAndTypeTimerAndAgentTask;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.conf.AgentConfigurationException;

/**
 * Unit tests for the {@link AnnotatedTimerAgent}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotatedTimerAgentTest
{
    @Mock
    AgentConfiguration configuration;

    AnnotatedTimerAgent agent;

    @Before
    public void setup()
    {
        Mockito.when(configuration.getType()).thenReturn("timer");
        Mockito.when(configuration.getInterval()).thenReturn("60 seconds");
    }

    @Test(expected = AgentConfigurationException.class)
    public void initForClassWithoutAgentTaskAnnotation()
    {
        Mockito.when(configuration.getAgentClass())
                .thenReturn(TestAgentWithNoNameAndTypeTimerAndNoAgentTask.class.getName());
        new AnnotatedTimerAgent(configuration);
    }

    @Test(expected = AgentConfigurationException.class)
    public void initForClassWithTwoAgentTaskAnnotations()
    {
        Mockito.when(configuration.getAgentClass())
                .thenReturn(TestAgentWithNoNameAndTypeTimerAndTwoAgentTasks.class.getName());
        new AnnotatedTimerAgent(configuration);
    }

    @Test
    public void runTaskForClassWithAgentTaskAnnotation()
    {
        Mockito.when(configuration.getAgentClass())
                .thenReturn(TestAgentWithNoNameAndTypeTimerAndAgentTask.class.getName());
        AnnotatedTimerAgent annotatedTimerAgent = new AnnotatedTimerAgent(configuration);

        assertNotNull(annotatedTimerAgent.getAnnotatedAgentInstance());
        assertNotNull(annotatedTimerAgent.getAnnotatedAgentTaskMethod());

        annotatedTimerAgent.runTask();
    }

    @Test()
    public void initForClassWithAgentTaskAnnotationAndPrivateConstructor()
    {
        Mockito.when(configuration.getAgentClass())
                .thenReturn(TestAgentWithAllCustomParamsAndPrivateConstructor.class.getName());
        TestUtils.assertException(AgentConfigurationException.class, null, NoSuchMethodException.class,
                () -> new AnnotatedTimerAgent(configuration));
    }

    @Test(expected = AgentConfigurationException.class)
    public void runForClassWithAgentTaskAnnotationAndPrivateAgentTask()
    {
        Mockito.when(configuration.getAgentClass())
                .thenReturn(TestAgentWithAllCustomParamsAndPrivateAgentTask.class.getName());
        new AnnotatedTimerAgent(configuration);
    }

    @Test
    public void toStringPrintsCustomString()
    {
        Mockito.when(configuration.getAgentClass())
                .thenReturn(TestAgentWithNoNameAndTypeTimerAndAgentTask.class.getName());
        AnnotatedTimerAgent annotatedTimerAgent = new AnnotatedTimerAgent(configuration);

        String string = annotatedTimerAgent.toString();
        assertEquals("AnnotatedTimerAgent$" + TestAgentWithNoNameAndTypeTimerAndAgentTask.class.getName(), string);
    }

}
