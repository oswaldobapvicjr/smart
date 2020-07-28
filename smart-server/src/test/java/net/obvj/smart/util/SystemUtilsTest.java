package net.obvj.smart.util;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static net.obvj.junit.utils.matchers.StringMatcher.containsAll;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.lang.Thread.State;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import net.obvj.smart.jmx.dto.ThreadDTO;

/**
 * Unit tests for the {@link SystemUtils} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class SystemUtilsTest
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
        ThreadInfo threadInfo = mock(ThreadInfo.class);
        when(threadInfo.getThreadId()).thenReturn(id);
        when(threadInfo.getThreadName()).thenReturn(name);
        when(threadInfo.getThreadState()).thenReturn(state);
        return threadInfo;
    }

    @Test
    public void testThreadDTOs()
    {
        assertTrue(SystemUtils.getSystemTheadsDTOs(ALL_THREADS).containsAll(ALL_DTOS));
    }

    @Test
    public void testNoInstancesAllowed()
    {
        assertThat(SystemUtils.class, instantiationNotAllowed());
    }

    @Test
    public void testGetJavaVersion()
    {
        assertThat(SystemUtils.getJavaVersion(), containsAll(org.apache.commons.lang3.SystemUtils.JAVA_RUNTIME_NAME,
                org.apache.commons.lang3.SystemUtils.JAVA_RUNTIME_VERSION,
                org.apache.commons.lang3.SystemUtils.JAVA_VM_NAME, org.apache.commons.lang3.SystemUtils.JAVA_VM_VERSION,
                org.apache.commons.lang3.SystemUtils.JAVA_VM_VENDOR));
    }

}
