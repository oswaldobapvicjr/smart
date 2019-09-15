package net.obvj.smart.jmx.dto;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Tests for the {@link ThreadDTO} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ThreadDTOTest
{
    public static final ThreadDTO THREAD1_1 = new ThreadDTO(1, "name1", "RUNNABLE");
    public static final ThreadDTO THREAD1_2 = new ThreadDTO(1, "name1", "RUNNABLE");

    public static final ThreadDTO THREAD2_1 = new ThreadDTO(2, "name2", "WAITING");

    /**
     * Test that the equals returns true for two similar objects
     */
    @Test
    public void testEquals()
    {
        assertTrue(THREAD1_1.equals(THREAD1_2));
    }

    /**
     * Test that the equals returns false for two different objects
     */
    @Test
    public void testNotEqual()
    {
        assertFalse(THREAD1_1.equals(THREAD2_1));
    }

    /**
     * Test the hashCode
     */
    @Test
    public void testEqualObjectsInASet()
    {
        Set<ThreadDTO> set = new HashSet<>();
        set.add(THREAD1_1);
        set.add(THREAD1_2);
        assertEquals(1, set.size());
    }

}
