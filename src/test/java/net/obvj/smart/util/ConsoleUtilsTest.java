package net.obvj.smart.util;

import org.junit.Test;

import net.obvj.smart.TestUtils;

/**
 * Unit tests for the {@link ConsoleUtils} class.
 * 
 * @author oswaldo.bapvic.jr
 */
public class ConsoleUtilsTest
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
        TestUtils.checkNoInstancesAllowed(ConsoleUtils.class, IllegalStateException.class, "Utility class");
    }
    
    @Test
    public void testReadFileFromClasspathReturnsContent()
    {
        TestUtils.assertStringContains(ConsoleUtils.readCustomHeaderLines().toString(), "S.M.A.R.T. Console");
    }

}
