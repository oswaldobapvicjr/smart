package net.obvj.smart.conf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.smart.agents.internal.BackgroundAgent;
import net.obvj.smart.agents.test.invalid.TestClassNotAgent;
import net.obvj.smart.agents.test.valid.DummyAgent;
import net.obvj.smart.agents.test.valid.TestAgentWithNoNameAndTypeTimerAndAgentTask;
import net.obvj.smart.conf.properties.SmartProperties;

/**
 * Unit tests for the {@link AnnotatedAgents} component.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AnnotatedAgentsTest
{
    private static final String TEST_PACKAGE = "net.obvj.smart.agents.test";
    private static final List<String> EXCPECTED_TEST_PACKAGE_AGENT_CLASSES = Arrays
            .asList(TestAgentWithNoNameAndTypeTimerAndAgentTask.class.getName(), DummyAgent.class.getName());
    private static final List<String> UNEXPECTED_TEST_PACKAGE_AGENT_CLASSES = Arrays
            .asList(TestClassNotAgent.class.getName());

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

        Map<String, AgentConfiguration> agentsByClassName = annotatedAgents.getAgentsByClassName();
        assertTrue(agentsByClassName.keySet().containsAll(EXCPECTED_TEST_PACKAGE_AGENT_CLASSES));
        assertTrue(agentsByClassName.keySet().containsAll(EXCPECTED_INTERNAL_PACKAGE_AGENT_CLASSES));
        agentsByClassName.values().forEach(config -> assertNotNull(config));

        assertFalse(agentsByClassName.keySet().containsAll(UNEXPECTED_TEST_PACKAGE_AGENT_CLASSES));
    }

    @Test
    public void findAnnotatedAgentClassesForInvalidPackage()
    {
        when(properties.getPropertiesListSplitBy(SmartProperties.AGENT_SEARCH_PACKAGES, ","))
                .thenReturn(Arrays.asList("invalid"));
        annotatedAgents = new AnnotatedAgents(properties);
        // At least the intenal package must be present
        assertEquals(1, annotatedAgents.getAgentsByClassName().size());
    }

    @Test(expected = AgentConfigurationException.class)
    public void toClassWithInvalidClass()
    {
        annotatedAgents = new AnnotatedAgents(properties);
        annotatedAgents.toClass("invalid");
    }

}
