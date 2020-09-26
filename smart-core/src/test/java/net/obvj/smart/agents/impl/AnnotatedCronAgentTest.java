package net.obvj.smart.agents.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.smart.agents.test.valid.TestAgentWithNoNameAndTypeCronAndAgentTask;
import net.obvj.smart.conf.AgentConfiguration;

/**
 * Unit tests for the {@link AnnotatedCronAgent}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotatedCronAgentTest
{
    @Mock
    AgentConfiguration configuration;

    AnnotatedCronAgent agent;

    @Before
    public void setup()
    {
        Mockito.when(configuration.getType()).thenReturn("cron");
        Mockito.when(configuration.getFrequency()).thenReturn("* * * * *");
    }

    @Test
    public void runTaskForClassWithAgentTaskAnnotation()
    {
        Mockito.when(configuration.getAgentClass())
                .thenReturn(TestAgentWithNoNameAndTypeCronAndAgentTask.class.getName());
        AnnotatedCronAgent annotatedAgent = new AnnotatedCronAgent(configuration);

        assertNotNull(annotatedAgent.getMetadata().getAgentInstance());
        assertNotNull(annotatedAgent.getMetadata().getAgentTaskMethod());

        annotatedAgent.runTask();
    }

    @Test
    public void toStringPrintsCustomString()
    {
        Mockito.when(configuration.getAgentClass())
                .thenReturn(TestAgentWithNoNameAndTypeCronAndAgentTask.class.getName());
        AnnotatedCronAgent annotatedAgent = new AnnotatedCronAgent(configuration);

        String string = annotatedAgent.toString();
        assertEquals("AnnotatedCronAgent$" + TestAgentWithNoNameAndTypeCronAndAgentTask.class.getName(), string);
    }

}
