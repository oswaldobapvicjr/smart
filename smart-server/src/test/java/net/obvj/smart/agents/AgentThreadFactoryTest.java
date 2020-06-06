package net.obvj.smart.agents;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test methods for the {@link AgentThreadFactory} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
public class AgentThreadFactoryTest
{

    private static final String AGENT_A = "AgentA";
    private static final String AGENT_B = "AgentB";
    private static final String THREAD_PREFIX = "Agent-";

    private static final Runnable RUNNABLE = PowerMockito.mock(Runnable.class);

    // Test subject
    private AgentThreadFactory threadFactory1 = new AgentThreadFactory(AGENT_A);
    private AgentThreadFactory threadFactory2 = new AgentThreadFactory(AGENT_B);

    /**
     * Tests the threads are created with unique, sequential names
     */
    @Test
    public void testThreadNames()
    {
        Thread process1Thread1 = threadFactory1.newThread(RUNNABLE);
        Thread process1Thread2 = threadFactory1.newThread(RUNNABLE);
        Thread process2Thread1 = threadFactory2.newThread(RUNNABLE);
        Thread process2Thread2 = threadFactory2.newThread(RUNNABLE);

        assertThat(process1Thread1.getName(), is(THREAD_PREFIX + AGENT_A + "-thread1"));
        assertThat(process1Thread2.getName(), is(THREAD_PREFIX + AGENT_A + "-thread2"));
        assertThat(process2Thread1.getName(), is(THREAD_PREFIX + AGENT_B + "-thread1"));
        assertThat(process2Thread2.getName(), is(THREAD_PREFIX + AGENT_B + "-thread2"));
    }

    /**
     * Tests the threads created by the factory are not daemon
     */
    @Test
    public void testThreadsAreNotDaemon()
    {
        Thread thread = threadFactory1.newThread(RUNNABLE);
        assertThat(thread.isDaemon(), is(false));
    }

}
