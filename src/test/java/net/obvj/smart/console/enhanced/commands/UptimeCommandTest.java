package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

/**
 * Unit tests for the {@link UptimeCommand} class
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AgentManagerJMXClient.class)
public class UptimeCommandTest
{
    // Test data
    private static final long ONE_DAY_IN_MILLIS = 86400000;
    private static final long ONE_HOUR_IN_MILLIS = 3600000;
    private static final long ONE_MINUTE_IN_MILLIS = 60000;
    private static final long ONE_SECOND_IN_MILLIS = 1000;

    /**
     * Prepares a mocked {@link AgentManagerJMXMBean}.
     * 
     * @param expectedUptime the expected value to be returned by the mock
     */
    private void expectUptime(long expectedUptime) throws IOException
    {
        AgentManagerJMXMBean agentManagerJMXBeanMock = PowerMockito.mock(AgentManagerJMXMBean.class);
        PowerMockito.when(agentManagerJMXBeanMock.getServerUptime()).thenReturn(expectedUptime);

        PowerMockito.mockStatic(AgentManagerJMXClient.class);
        PowerMockito.when(AgentManagerJMXClient.getMBeanProxy()).thenReturn(agentManagerJMXBeanMock);
    }

    /**
     * Creates a new command that will print its output onto the given StringWriter.
     * 
     * @param out the StringWriter to which the command will print
     * @return an {@link UptimeCommand} for testing
     */
    private UptimeCommand newCommandWithOutput(StringWriter out)
    {
        UptimeCommand command = new UptimeCommand();
        command.setParent(new Commands(new PrintWriter(out)));
        return command;
    }

    @Test
    public void testFormatOutputOneDay()
    {
        assertEquals("86400000 milliseconds", UptimeCommand.formatOutput(ONE_DAY_IN_MILLIS, "milliseconds"));
        assertEquals("86400 seconds", UptimeCommand.formatOutput(ONE_DAY_IN_MILLIS, "seconds"));
        assertEquals("1440 minutes", UptimeCommand.formatOutput(ONE_DAY_IN_MILLIS, "minutes"));
        assertEquals("24 hours", UptimeCommand.formatOutput(ONE_DAY_IN_MILLIS, "hours"));
        assertEquals("1 day", UptimeCommand.formatOutput(ONE_DAY_IN_MILLIS, "days"));
    }

    @Test
    public void testFormatOutputOneHour()
    {
        assertEquals("3600000 milliseconds", UptimeCommand.formatOutput(ONE_HOUR_IN_MILLIS, "milliseconds"));
        assertEquals("3600 seconds", UptimeCommand.formatOutput(ONE_HOUR_IN_MILLIS, "seconds"));
        assertEquals("60 minutes", UptimeCommand.formatOutput(ONE_HOUR_IN_MILLIS, "minutes"));
        assertEquals("1 hour", UptimeCommand.formatOutput(ONE_HOUR_IN_MILLIS, "hours"));
        assertEquals("Less than 1 day", UptimeCommand.formatOutput(ONE_HOUR_IN_MILLIS, "days"));
    }

    @Test
    public void testFormatOutputOneMinute()
    {
        assertEquals("60000 milliseconds", UptimeCommand.formatOutput(ONE_MINUTE_IN_MILLIS, "milliseconds"));
        assertEquals("60 seconds", UptimeCommand.formatOutput(ONE_MINUTE_IN_MILLIS, "seconds"));
        assertEquals("1 minute", UptimeCommand.formatOutput(ONE_MINUTE_IN_MILLIS, "minutes"));
        assertEquals("Less than 1 hour", UptimeCommand.formatOutput(ONE_MINUTE_IN_MILLIS, "hours"));
        assertEquals("Less than 1 day", UptimeCommand.formatOutput(ONE_MINUTE_IN_MILLIS, "days"));
    }

    @Test
    public void testFormatOutputOneSecond()
    {
        assertEquals("1000 milliseconds", UptimeCommand.formatOutput(ONE_SECOND_IN_MILLIS, "milliseconds"));
        assertEquals("1 second", UptimeCommand.formatOutput(ONE_SECOND_IN_MILLIS, "seconds"));
        assertEquals("Less than 1 minute", UptimeCommand.formatOutput(ONE_SECOND_IN_MILLIS, "minutes"));
        assertEquals("Less than 1 hour", UptimeCommand.formatOutput(ONE_SECOND_IN_MILLIS, "hours"));
        assertEquals("Less than 1 day", UptimeCommand.formatOutput(ONE_SECOND_IN_MILLIS, "days"));
    }

    @Test
    public void testCommandExecutionWithJMXCallAndDefautTimeUnit() throws IOException
    {
        expectUptime(ONE_DAY_IN_MILLIS);

        StringWriter out = new StringWriter();
        UptimeCommand command = newCommandWithOutput(out);
        command.run();

        assertEquals("86400000 milliseconds", out.toString().trim());
    }

    @Test
    public void testCommandExecutionWithJMXCallAndDifferentTimeout() throws IOException
    {
        expectUptime(ONE_DAY_IN_MILLIS);

        StringWriter out = new StringWriter();
        UptimeCommand command = newCommandWithOutput(out);
        command.setTimeUnit("hours");
        command.run();

        assertEquals("24 hours", out.toString().trim());
    }

}
