package net.obvj.smart.util;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import net.obvj.smart.agents.test.invalid.*;
import net.obvj.smart.agents.test.valid.DummyAgent;
import net.obvj.smart.agents.test.valid.TestAgentWithNoNameAndTypeTimerAndAgentTask;
import net.obvj.smart.conf.annotation.Agent;

/**
 * Unit tests for the {@link AnnotationUtils}
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AnnotationUtilsTest
{
    private static final String TEST_AGENTS_PACKAGE = "net.obvj.smart.agents.test";

    private static final List<String> EXCPECTED_AGENT_CLASS_NAMES = Arrays.asList(
            TestAgentWithAllCustomParams.class.getName(),
            TestAgentWithAllCustomParamsAndPrivateConstructor.class.getName(),
            TestAgentWithCustomNameAndType.class.getName(),
            TestAgentWithAllCustomParamsAndPrivateAgentTask.class.getName(),
            TestAgentWithNoNameAndTypeTimerAndAgentTask.class.getName(),
            TestAgentWithNoNameAndTypeTimerAndNoAgentTask.class.getName(),
            TestAgentWithNoNameAndTypeTimerAndTwoAgentTasks.class.getName(), TestAgentWithNoType.class.getName(),
            DummyAgent.class.getName());

    private static final List<String> UNEXPECTED_AGENT_CLASS_NAMES = Arrays.asList(TestClassNotAgent.class.getName());

    @Test
    public void testNoInstancesAllowed()
    {
        assertThat(AnnotationUtils.class, instantiationNotAllowed());
    }

    @Test
    public void findClassesWithAgentAnnotation()
    {
        Set<String> classNames = AnnotationUtils.findClassesWithAnnotation(Agent.class, TEST_AGENTS_PACKAGE);
        assertTrue(classNames.containsAll(EXCPECTED_AGENT_CLASS_NAMES));
        assertFalse("A class without the @Agent annotation should not be retrieved",
                classNames.containsAll(UNEXPECTED_AGENT_CLASS_NAMES));
    }

}
