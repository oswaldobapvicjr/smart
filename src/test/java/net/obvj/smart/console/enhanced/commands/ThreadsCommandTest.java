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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.jmx.dto.ThreadDTO;
import net.obvj.smart.util.ApplicationContextFacade;

/**
 * Unit tests for the {@link ThreadsCommand} class
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ApplicationContextFacade.class)
public class ThreadsCommandTest
{
    // Test data
    private static final ThreadDTO THREAD1 = new ThreadDTO(1, "name1", "RUNNABLE");
    private static final ThreadDTO THREAD2 = new ThreadDTO(2, "name2", "WAITING");

    private static final List<ThreadDTO> ALL_THREADS_LIST = Arrays.asList(THREAD1, THREAD2);

    private static final String THREAD1_EXPECTED_STR_COMP = "1name1RUNNABLE";
    private static final String THREAD2_EXPECTED_STR_COMP = "2name2WAITING";

    private StringWriter sw = new StringWriter();

    @Mock
    private AgentManagerJMXMBean jmx;
    @Mock
    private AgentManagerJMXClient client;
    @Spy
    private Commands parent = new Commands(new PrintWriter(sw));

    @InjectMocks
    private ThreadsCommand command;

    @Before
    public void setup() throws IOException
    {
        mockStatic(ApplicationContextFacade.class);
        when(ApplicationContextFacade.getBean(AgentManagerJMXClient.class)).thenReturn(client);
        when(client.getMBeanProxy()).thenReturn(jmx);
    }

    @Test
    public void testListAllThreads() throws IOException
    {
        when(jmx.getAllThreadsInfo()).thenReturn(ALL_THREADS_LIST);
        command.run();

        // Trim variable padding spaces for testing
        String out = sw.toString().replace(" ", "");
        assertTrue(out.contains(THREAD1_EXPECTED_STR_COMP));
        assertTrue(out.contains(THREAD2_EXPECTED_STR_COMP));
    }

}
