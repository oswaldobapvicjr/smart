package net.obvj.smart.console;

import static net.obvj.smart.console.MgmtConsoleWorkerThreadFactory.NEXT_SEQUENCE_NUMBER;
import static net.obvj.smart.console.MgmtConsoleWorkerThreadFactory.THREAD_NAME_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for the {@link MgmtConsoleWorkerThreadFactory} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
public class MgmtConsoleWorkerThreadFactoryTest
{
    private static final Runnable RUNNABLE = PowerMockito.mock(Runnable.class);

    private MgmtConsoleWorkerThreadFactory threadFactory = new MgmtConsoleWorkerThreadFactory();

    @Test
    public void testThreadNames()
    {
        int nextNumber = NEXT_SEQUENCE_NUMBER.get();

        assertEquals(THREAD_NAME_PREFIX + nextNumber++, threadFactory.newThread(RUNNABLE).getName());
        assertEquals(THREAD_NAME_PREFIX + nextNumber, threadFactory.newThread(RUNNABLE).getName());
    }

    /**
     * Tests the threads created by the factory are not daemon
     */
    @Test
    public void testThreadNotDaemon()
    {
        assertFalse(threadFactory.newThread(RUNNABLE).isDaemon());
    }

    /**
     * Tests the threads created by the factory are minimum-priority
     */
    @Test
    public void testThreadPriority()
    {
        assertEquals(Thread.MIN_PRIORITY, threadFactory.newThread(RUNNABLE).getPriority());
    }
}
