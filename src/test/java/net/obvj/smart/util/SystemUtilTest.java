package net.obvj.smart.util;

import static org.junit.Assert.assertTrue;

import java.lang.Thread.State;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import net.obvj.smart.TestUtil;
import net.obvj.smart.jmx.dto.ThreadDTO;

/**
 * Unit tests for the {@link SystemUtil} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class SystemUtilTest
{
    private static final ThreadInfo THREAD1 = mockThreadInfo(1, "name1", State.RUNNABLE);
    private static final ThreadInfo THREAD2 = mockThreadInfo(2, "name2", State.TIMED_WAITING);
    private static final ThreadInfo[] ALL_THREADS = new ThreadInfo[] { THREAD1, THREAD2 };

    private static final ThreadDTO DTO1 = new ThreadDTO(1, "name1", "RUNNABLE");
    private static final ThreadDTO DTO2 = new ThreadDTO(2, "name2", "TIMED_WAITING");
    private static final Collection<ThreadDTO> ALL_DTOS = Arrays.asList(DTO1, DTO2);

    /**
     * Produces mocked ThreadInfo objects, since these cannot be initialized without a Thread
     */
    private static ThreadInfo mockThreadInfo(long id, String name, State state)
    {
        ThreadInfo threadInfo = PowerMockito.mock(ThreadInfo.class);
        PowerMockito.when(threadInfo.getThreadId()).thenReturn(id);
        PowerMockito.when(threadInfo.getThreadName()).thenReturn(name);
        PowerMockito.when(threadInfo.getThreadState()).thenReturn(state);
        return threadInfo;
    }

    @Test
    public void testThreadDTOs()
    {
        assertTrue(SystemUtil.getSystemTheadsDTOs(ALL_THREADS).containsAll(ALL_DTOS));
    }

    /**
     * Tests that no instances of this utility class are created
     *
     * @throws Exception in case of error getting constructor metadata or instantiating the
     *                   private constructor via Reflection
     */
    @Test
    public void testNoInstancesAllowed() throws Exception
    {
        TestUtil.testNoInstancesAllowed(SystemUtil.class, IllegalStateException.class, "Utility class");
    }

}
