package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit4.SpringRunner;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

/**
 * Unit tests for the {@link UptimeCommand} class
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(SpringRunner.class)
public class UptimeCommandTest
{
    // Test data
    private static final long ONE_DAY_IN_MILLIS = 86400000;
    private static final long ONE_HOUR_IN_MILLIS = 3600000;
    private static final long ONE_MINUTE_IN_MILLIS = 60000;
    private static final long ONE_SECOND_IN_MILLIS = 1000;

    private StringWriter sw = new StringWriter();

    @Mock
    private AgentManagerJMXMBean jmx;
    @Mock
    private AgentManagerJMXClient client;
    @Spy
    private Commands parent = new Commands(new PrintWriter(sw));

    @InjectMocks
    private UptimeCommand command;

    @Before
    public void setup() throws IOException
    {
        when(client.getMBeanProxy()).thenReturn(jmx);
    }

    /**
     * Prepares a mocked {@link AgentManagerJMXMBean}.
     * 
     * @param expectedUptime the expected value to be returned by the mock
     */
    private void expectUptime(long expectedUptime) throws IOException
    {
        when(jmx.getServerUptime()).thenReturn(expectedUptime);
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
        command.run();
        assertEquals("86400000 milliseconds", sw.toString().trim());
    }

    @Test
    public void testCommandExecutionWithJMXCallAndDifferentTimeout() throws IOException
    {
        expectUptime(ONE_DAY_IN_MILLIS);
        command.setTimeUnit("hours");
        command.run();
        assertEquals("24 hours", sw.toString().trim());
    }

}
