package net.obvj.smart.util;

import org.junit.Test;

/**
 * Unit tests for the {@link ConsoleUtil} class.
 * 
 * @author oswaldo.bapvic.jr
 */
public class ConsoleUtilTest
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
        UtilitiesCommons.testNoInstancesAllowed(ConsoleUtil.class, IllegalStateException.class, "Utility class");
    }

}