package net.obvj.smart.util;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Test;

import net.obvj.junit.utils.TestUtils;

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
        TestUtils.assertNoInstancesAllowed(ConsoleUtils.class, IllegalStateException.class, "Utility class");
    }

    @Test
    public void testReadFileFromClasspathReturnsContent()
    {
        TestUtils.assertStringContains(ConsoleUtils.readCustomHeaderLines().toString(), "S.M.A.R.T. Console");
    }

    @Test
    public void testReadFileListWithFileNotFound()
    {
        assertEquals(Collections.emptyList(), ConsoleUtils.readFileLines("invalidFile.if"));
    }

}
