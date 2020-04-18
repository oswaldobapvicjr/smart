package net.obvj.smart.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.obvj.junit.utils.TestUtils;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

/**
 * Unit tests for the {@link ClientApplicationContextFacade}
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ClientApplicationContextFacadeTest
{
    /**
     * Tests that no instances of this utility class are created
     *
     * @throws Exception in case of error getting constructor metadata or instantiating the
     *                   private constructor via Reflection
     */
    @Test
    public void testNoInstancesAllowed() throws Exception
    {
        TestUtils.assertNoInstancesAllowed(ClientApplicationContextFacade.class, IllegalStateException.class,
                "Utility class");
    }

    @Test
    public void testRetrieveAgentManagerJMXClientAllowed()
    {
        assertNotNull(ClientApplicationContextFacade.getBean(AgentManagerJMXClient.class));
    }

}
