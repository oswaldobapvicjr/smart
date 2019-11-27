package net.obvj.smart.util;

import org.junit.Test;

import net.obvj.smart.TestUtils;

/**
 * Unit tests for the {@link AnnotationUtils}
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AnnotationUtilsFacadeTest
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
        TestUtils.checkNoInstancesAllowed(AnnotationUtils.class, IllegalStateException.class, "Utility class");
    }

}
