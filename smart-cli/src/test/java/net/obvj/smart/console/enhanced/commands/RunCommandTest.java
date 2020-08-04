package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertTrue;
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
import org.springframework.test.context.junit4.SpringRunner;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

/**
 * Unit tests for the {@link RunCommand} class
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(SpringRunner.class)
public class RunCommandTest
{
    private StringWriter sw = new StringWriter();

    @Mock
    private AgentManagerJMXMBean jmx;
    @Mock
    private AgentManagerJMXClient client;
    @Spy
    private Commands parent = new Commands(new PrintWriter(sw));

    @InjectMocks
    private RunCommand command;

    @Before
    public void setup() throws IOException
    {
        when(client.getMBeanProxy()).thenReturn(jmx);
    }

    /**
     * Tests that the correct method from AgentManagerJMXBean is called
     */
    @Test
    public void testCommandExecutionJMXCall() throws IOException
    {
        when(jmx.getAgentStatusStr("AgentName")).thenReturn("{\"lastExecutionDuration\":\"5 second(s)\"}");
        command.setAgent("AgentName");
        command.run();
        Mockito.verify(jmx, Mockito.times(1)).runNow("AgentName");
    }

    /**
     * Tests the message printed to the console when an exception is thrown by the
     * AgentManagerJMXBean
     */
    @Test
    public void testCommandExecutionJMXCallException() throws IOException
    {
        Mockito.doThrow(new IllegalArgumentException("Invalid agent")).when(jmx).runNow("InvalidName");

        command.setAgent("InvalidName");
        command.run();

        assertTrue(sw.toString().contains("Invalid agent"));
    }

}
