package net.obvj.smart.conf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.smart.agents.internal.BackgroundAgent;
import net.obvj.smart.agents.test.valid.DummyDaemonAgent;
import net.obvj.smart.agents.test.valid.TestAgentWithNoNameAndTypeTimerAndAgentTask;
import net.obvj.smart.agents.test.valid.TestAgentWithTypeDeterminedBySupertypeTimer;

/**
 * Unit tests for the {@link AnnotatedAgents} component.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotatedAgentsTest
{
    private static final String TEST_PACKAGE = "net.obvj.smart.agents.test.valid";
    private static final List<String> EXCPECTED_TEST_PACKAGE_AGENT_CLASSES = Arrays.asList(
            TestAgentWithNoNameAndTypeTimerAndAgentTask.class.getName(),
            TestAgentWithTypeDeterminedBySupertypeTimer.class.getName());
    private static final List<String> UNEXPECTED_TEST_PACKAGE_AGENT_CLASSES = Arrays
            .asList(DummyDaemonAgent.class.getName());

    private static final String INTERNAL_PACKAGE = "net.obvj.smart.agents.internal";
    private static final List<String> EXCPECTED_INTERNAL_PACKAGE_AGENT_CLASSES = Arrays
            .asList(BackgroundAgent.class.getName());

    @Mock
    private SmartProperties properties;

    private AnnotatedAgents annotatedAgents;

    @Before
    public void setup()
    {
    }

    @Test
    public void findAnnotatedAgentClassesForTestPackage()
    {
        when(properties.getPropertiesListSplitBy(SmartProperties.AGENT_SEARCH_PACKAGES, ","))
                .thenReturn(Arrays.asList(TEST_PACKAGE));

        annotatedAgents = new AnnotatedAgents(properties);
        assertTrue(annotatedAgents.getAgentsByClassName().keySet().containsAll(EXCPECTED_TEST_PACKAGE_AGENT_CLASSES));
        annotatedAgents.getAgentsByClassName().values().forEach(config -> assertNotNull(config));

        assertFalse(annotatedAgents.getAgentsByClassName().keySet().containsAll(UNEXPECTED_TEST_PACKAGE_AGENT_CLASSES));
    }

    @Test
    public void findAnnotatedAgentClassesForTestPackageAndInternalPackage()
    {
        when(properties.getPropertiesListSplitBy(SmartProperties.AGENT_SEARCH_PACKAGES, ","))
                .thenReturn(Arrays.asList(TEST_PACKAGE, INTERNAL_PACKAGE));
        annotatedAgents = new AnnotatedAgents(properties);

        assertTrue(annotatedAgents.getAgentsByClassName().keySet().containsAll(EXCPECTED_TEST_PACKAGE_AGENT_CLASSES));
        assertTrue(
                annotatedAgents.getAgentsByClassName().keySet().containsAll(EXCPECTED_INTERNAL_PACKAGE_AGENT_CLASSES));
        annotatedAgents.getAgentsByClassName().values().forEach(config -> assertNotNull(config));

        assertFalse(annotatedAgents.getAgentsByClassName().keySet().containsAll(UNEXPECTED_TEST_PACKAGE_AGENT_CLASSES));
    }

    @Test
    public void findAnnotatedAgentClassesForInvalidPackage()
    {
        when(properties.getPropertiesListSplitBy(SmartProperties.AGENT_SEARCH_PACKAGES, ","))
                .thenReturn(Arrays.asList("invalid"));
        annotatedAgents = new AnnotatedAgents(properties);
        assertEquals(0, annotatedAgents.getAgentsByClassName().size());
    }

}
