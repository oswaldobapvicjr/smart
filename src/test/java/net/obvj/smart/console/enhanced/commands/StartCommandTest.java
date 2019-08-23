package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

/**
 * Unit tests for the {@link StartCommand} class
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AgentManagerJMXClient.class)
public class StartCommandTest
{
    @Mock
    private AgentManagerJMXMBean agentManagerJMXBean;

    @Before
    public void setup() throws IOException
    {
        PowerMockito.mockStatic(AgentManagerJMXClient.class);
        PowerMockito.when(AgentManagerJMXClient.getMBeanProxy()).thenReturn(agentManagerJMXBean);
    }

    /**
     * Creates a new command that will print its output onto the given StringWriter.
     * 
     * @param out the StringWriter to which the command will print
     * @return a {@link StartCommand} for testing
     */
    private StartCommand newCommandWithOutput(StringWriter out)
    {
        StartCommand command = new StartCommand();
        command.setParent(new Commands(new PrintWriter(out)));
        return command;
    }

    /**
     * Tests that the correct method from AgentManagerJMXBean is called
     */
    @Test
    public void testCommandExecutionJMXCall() throws IOException
    {
        StartCommand command = newCommandWithOutput(new StringWriter());
        command.setAgent("AgentName");
        command.run();
        Mockito.verify(agentManagerJMXBean, Mockito.times(1)).startAgent("AgentName");
    }

    /**
     * Tests the message printed to the console when an exception is thrown by the
     * AgentManagerJMXBean
     */
    @Test
    public void testCommandExecutionJMXCallException() throws IOException
    {
        Mockito.doThrow(new IllegalArgumentException("message1")).when(agentManagerJMXBean).startAgent("InvalidName");
        
        StringWriter out = new StringWriter();
        StartCommand command = newCommandWithOutput(out);
        command.setAgent("InvalidName");
        command.run();
        
        assertTrue(out.toString().contains("message1"));
    }

}
