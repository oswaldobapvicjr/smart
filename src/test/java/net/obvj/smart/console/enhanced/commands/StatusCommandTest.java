package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeoutException;

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
 * Unit tests for the {@link StatusCommand} class
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AgentManagerJMXClient.class)
public class StatusCommandTest
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
     * @return a {@link StatusCommand} for testing
     */
    private StatusCommand newCommandWithOutput(StringWriter out)
    {
        StatusCommand command = new StatusCommand();
        command.setParent(new Commands(new PrintWriter(out)));
        return command;
    }

    /**
     * Tests that the correct method from AgentManagerJMXBean is called
     */
    @Test
    public void testCommandExecutionJMXCall() throws IOException, TimeoutException
    {
        Mockito.doReturn("statusString1").when(agentManagerJMXBean).getAgentStatusStr("AgentName");

        StringWriter out = new StringWriter();
        StatusCommand command = newCommandWithOutput(out);
        command.setAgent("AgentName");
        command.run();

        assertEquals("statusString1", out.toString().trim());
    }

    /**
     * Tests the message printed to the console when an IllegalArgumentException is thrown by
     * the AgentManagerJMXBean
     */
    @Test
    public void testCommandExecutionJMXCallIllegalArgumentException() throws IOException, TimeoutException
    {
        Mockito.doThrow(new IllegalArgumentException("Invalid name")).when(agentManagerJMXBean)
                .getAgentStatusStr("AgentName");

        StringWriter out = new StringWriter();
        StatusCommand command = newCommandWithOutput(out);
        command.setAgent("AgentName");
        command.run();

        assertTrue(out.toString().contains("Invalid name"));
    }

}
