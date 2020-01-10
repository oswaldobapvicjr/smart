package net.obvj.smart.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import net.obvj.smart.TestUtils;
import net.obvj.smart.manager.AgentManager;

/**
 * Unit tests for the {@link ApplicationContextFacade}
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ApplicationContextFacadeTest
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
        TestUtils.assertNoInstancesAllowed(ApplicationContextFacade.class, IllegalStateException.class, "Utility class");
    }
    
    @Test
    public void testRetrieveAgentManagerBean()
    {
        assertNotNull(ApplicationContextFacade.getBean(AgentManager.class));
    }

}
