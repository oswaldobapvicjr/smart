package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.jmx.dto.ThreadDTO;

/**
 * Unit tests for the {@link ThreadCommand} class
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AgentManagerJMXClient.class)
public class ThreadsCommandTest
{
    // Test data
    
    private static final ThreadDTO THREAD1 = new ThreadDTO(1, "name1", "RUNNABLE");
    private static final ThreadDTO THREAD2 = new ThreadDTO(2, "name2", "WAITING");

    private static final List<ThreadDTO> ALL_THREADS_LIST = Arrays.asList(THREAD1, THREAD2);

    private static final String THREAD1_EXPECTED_STR_COMP = "1name1RUNNABLE";
    private static final String THREAD2_EXPECTED_STR_COMP = "2name2WAITING";

    @Mock
    private AgentManagerJMXMBean agentManagerJMXBean;

    @Before
    public void setup() throws IOException
    {
        mockStatic(AgentManagerJMXClient.class);
        when(AgentManagerJMXClient.getMBeanProxy()).thenReturn(agentManagerJMXBean);
        when(agentManagerJMXBean.getAllThreadsInfo()).thenReturn(ALL_THREADS_LIST);
    }

    /**
     * Creates a new command that will print its output onto the given StringWriter.
     * 
     * @param out the StringWriter to which the command will print
     * @return a {@link ThreadsCommand} for testing
     */
    private ThreadsCommand newCommandWithOutput(StringWriter out)
    {
        ThreadsCommand command = new ThreadsCommand();
        command.setParent(new Commands(new PrintWriter(out)));
        return command;
    }

    @Test
    public void testListAllThreads() throws IOException
    {
        StringWriter sw = new StringWriter();
        ThreadsCommand command = newCommandWithOutput(sw);
        command.run();

        // Trim variable padding spaces for testing
        String out = sw.toString().replace(" ", "");
        assertTrue(out.contains(THREAD1_EXPECTED_STR_COMP));
        assertTrue(out.contains(THREAD2_EXPECTED_STR_COMP));
    }

}
