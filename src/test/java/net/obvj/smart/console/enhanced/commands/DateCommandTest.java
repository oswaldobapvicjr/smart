package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
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
@PrepareForTest(AgentManagerJMXClient.class)
public class DateCommandTest
{
    // Test data
    private static final String EXPECTED_DATE = "2019-08-17 00:45:00";

    // Support mock
    private AgentManagerJMXMBean jmx = PowerMockito.mock(AgentManagerJMXMBean.class);

    // Test subject
    DateCommand command = new DateCommand();

    @Before
    public void setup() throws IOException
    {
        PowerMockito.mockStatic(AgentManagerJMXClient.class);
        PowerMockito.when(AgentManagerJMXClient.getMBeanProxy()).thenReturn(jmx);
    }

    @Test
    public void testDateCommandOutput() throws IOException
    {
        StringWriter sw = new StringWriter();
        command.setParent(new Commands(new PrintWriter(sw)));

        PowerMockito.when(jmx.getServerDate()).thenReturn(EXPECTED_DATE);

        command.run();
        assertTrue(sw.toString().contains(EXPECTED_DATE));
    }

}
