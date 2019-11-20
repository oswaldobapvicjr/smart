package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.util.ApplicationContextFacade;

/**
 * Unit tests for the {@link StartCommand} class
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ApplicationContextFacade.class)
public class StartCommandTest
{
    private StringWriter sw = new StringWriter();

    @Mock
    private AgentManagerJMXMBean jmx;
    @Mock
    private AgentManagerJMXClient client;
    @Spy
    private Commands parent = new Commands(new PrintWriter(sw));

    @InjectMocks
    private StartCommand command;

    @Before
    public void setup() throws IOException
    {
        mockStatic(ApplicationContextFacade.class);
        when(ApplicationContextFacade.getBean(AgentManagerJMXClient.class)).thenReturn(client);
        when(client.getMBeanProxy()).thenReturn(jmx);
    }

    /**
     * Tests that the correct method from AgentManagerJMXBean is called
     */
    @Test
    public void testCommandExecutionJMXCall() throws IOException
    {
        command.setAgent("AgentName");
        command.run();
        Mockito.verify(jmx, Mockito.times(1)).startAgent("AgentName");
    }

    /**
     * Tests the message printed to the console when an exception is thrown by the
     * AgentManagerJMXBean
     */
    @Test
    public void testCommandExecutionJMXCallException() throws IOException
    {
        Mockito.doThrow(new IllegalArgumentException("message1")).when(jmx).startAgent("InvalidName");
        
        command.setAgent("InvalidName");
        command.run();
        
        assertTrue(sw.toString().contains("message1"));
    }

}
