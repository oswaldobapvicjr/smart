package net.obvj.smart.agents.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.smart.TestUtils;
import net.obvj.smart.agents.test.TestAgentWithAllCustomParamsAndPrivateConstructor;
import net.obvj.smart.agents.test.TestAgentWithNoNameAndTypeTimerAndAgentTask;
import net.obvj.smart.agents.test.TestAgentWithNoNameAndTypeTimerAndNoAgentTask;
import net.obvj.smart.agents.test.TestAgentWithNoNameAndTypeTimerAndTwoAgentTasks;
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
        new AnnotatedTimerAgent(configuration).runTask();
    }

    @Test()
    public void initForClassWithAgentTaskAnnotationAndPrivateConstructor()
    {
        Mockito.when(configuration.getAgentClass())
                .thenReturn(TestAgentWithAllCustomParamsAndPrivateConstructor.class.getName());
        TestUtils.assertException(AgentConfigurationException.class, null, NoSuchMethodException.class,
                () -> new AnnotatedTimerAgent(configuration));
    }

}
