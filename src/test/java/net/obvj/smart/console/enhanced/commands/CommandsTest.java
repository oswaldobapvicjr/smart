package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import jline.console.ConsoleReader;
import net.obvj.smart.TestUtil;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

/**
 * Unit tests for the {@link Commands} class
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AgentManagerJMXClient.class)
public class CommandsTest
{
    @Mock
    ConsoleReader consoleReader;
    @Mock
    Writer writer;

    @Before
    public void setup()
    {
        mockStatic(AgentManagerJMXClient.class);
    }

    @Test
    public void testGetConsoleReader()
    {
        Mockito.when(consoleReader.getOutput()).thenReturn(writer);
        assertEquals(consoleReader, new Commands(consoleReader).getConsoleReader());
    }

    @Test
    public void testRun()
    {
        StringWriter sw = new StringWriter();
        new Commands(new PrintWriter(sw)).run();
        TestUtil.assertStringContains(sw.toString(), "Available commands:", "agents", "clear", "date", "status",
                "start", "stop", "reset", "run", "threads", "uptime", "Press <Ctrl> + D to exit");
    }

}
