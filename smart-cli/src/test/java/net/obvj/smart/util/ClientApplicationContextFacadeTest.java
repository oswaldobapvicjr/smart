package net.obvj.smart.util;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;

/**
 * Unit tests for the {@link ClientApplicationContextFacade}
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ClientApplicationContextFacadeTest
{
    @Test
    public void testNoInstancesAllowed()
    {
        assertThat(ClientApplicationContextFacade.class, instantiationNotAllowed());
    }

    @Test
    public void testRetrieveAgentManagerJMXClientAllowed()
    {
        assertNotNull(ClientApplicationContextFacade.getBean(AgentManagerJMXClient.class));
    }

}
