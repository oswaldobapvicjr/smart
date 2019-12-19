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
import org.mockito.Spy;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

/**
 * Unit tests for the {@link DateCommand} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
public class DateCommandTest
{
    // Test data
    private static final String EXPECTED_DATE = "2019-08-17 00:45:00";

    private StringWriter sw = new StringWriter();

    @Mock
    private AgentManagerJMXMBean jmx;
    @Mock
    private AgentManagerJMXClient client;
    @Spy
    private Commands parent = new Commands(new PrintWriter(sw));

    @InjectMocks
    private DateCommand command;

    @Before
    public void setup() throws IOException
    {
        when(client.getMBeanProxy()).thenReturn(jmx);
    }

    @Test
    public void testDateCommandOutput() throws IOException
    {
        when(jmx.getServerDate()).thenReturn(EXPECTED_DATE);
        command.run();
        assertTrue(sw.toString().contains(EXPECTED_DATE));
    }

}
