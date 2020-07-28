package net.obvj.smart.util;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.obvj.smart.manager.AgentManager;

/**
 * Unit tests for the {@link ApplicationContextFacade}
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ApplicationContextFacadeTest
{
    @Test
    public void testNoInstancesAllowed()
    {
        assertThat(ApplicationContextFacade.class, instantiationNotAllowed());
    }

    @Test
    public void testRetrieveAgentManagerBean()
    {
        assertNotNull(ApplicationContextFacade.getBean(AgentManager.class));
    }

}
