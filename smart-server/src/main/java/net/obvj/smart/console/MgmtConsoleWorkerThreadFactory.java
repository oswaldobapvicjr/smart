package net.obvj.smart.console;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An object that creates new threads for the {@link CommandWorker}.
 * <p>
 * The threads created by this factory are identified with a common constant and unique,
 * sequential number.
 * <p>
 * The threads are also set up as non-daemon, to secure Process finalization before system
 * shutdown, and minimum priority.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class MgmtConsoleWorkerThreadFactory implements ThreadFactory
{
    protected static final String THREAD_NAME_PREFIX = "MgmtConsoleWorker(classic)-T";

    protected static final AtomicInteger NEXT_SEQUENCE_NUMBER = new AtomicInteger(1);

    public Thread newThread(Runnable runnable)
    {
        Thread thread = new Thread(runnable, newThreadName());
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(false);
        return thread;
    }

    private String newThreadName()
    {
        return THREAD_NAME_PREFIX + NEXT_SEQUENCE_NUMBER.getAndIncrement();
    }
}
