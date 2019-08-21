package net.obvj.smart.console.enhanced;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.console.enhanced.EnhancedManagementConsole.Mode;
import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AgentManagerJMXClient.class)
public class EnhancedManagementConsoleTest
{

    @Test
    public void testParseArgs()
    {
        assertEquals("start DummyAgent", EnhancedManagementConsole.parseArgs("start", "DummyAgent"));
        assertEquals("agents", EnhancedManagementConsole.parseArgs("agents"));
        assertEquals("", EnhancedManagementConsole.parseArgs());
    }

    @Test
    public void testAssignableMode() throws IOException
    {
        assertEquals(Mode.INTERACTIVE, new EnhancedManagementConsole().getMode());
        assertEquals(Mode.INTERACTIVE, new EnhancedManagementConsole("").getMode());
        assertEquals(Mode.SINGLE_COMMAND, new EnhancedManagementConsole("agents").getMode());
    }

    @Test
    public void testPrintHeaderOnInteractiveMode() throws IOException
    {
        // Building a console with interactive mode
        EnhancedManagementConsole console = new EnhancedManagementConsole();

        // Mocking the method that reads custom header file content only
        console = PowerMockito.spy(console);
        PowerMockito.when(console.readCustomHeaderLines()).thenReturn(Arrays.asList("Header line 1", "Header line 2"));

        StringWriter sw = new StringWriter();
        console.printHeader(sw);

        String header = sw.toString();
        assertTrue(header.contains("Header line 1"));
        assertTrue(header.contains("Header line 2"));
        assertTrue(header.contains("Hit <Tab> for a list of available commands"));
        assertTrue(header.contains("Press <Ctrl> + D to quit the console."));
    }

    @Test
    public void testPrintHeaderOnSingleCommandMode() throws IOException
    {
        // Building a console with single-command mode
        EnhancedManagementConsole console = new EnhancedManagementConsole("threads");

        // Mocking the method that reads custom header file content only
        console = PowerMockito.spy(console);
        PowerMockito.when(console.readCustomHeaderLines()).thenReturn(Arrays.asList("Header line 1", "Header line 2"));

        StringWriter sw = new StringWriter();
        console.printHeader(sw);

        String header = sw.toString();
        assertTrue(header.contains("Header line 1"));
        assertTrue(header.contains("Header line 2"));

        // Interactive-mode specific hints shall not be printed on single-command mode
        assertFalse(header.contains("Hit <Tab> for a list of available commands"));
        assertFalse(header.contains("Press <Ctrl> + D to quit the console."));
    }
    
    @Test
    public void testHandleCommandLine() throws IOException
    {
        // Mock
        AgentManagerJMXMBean agentManagerJMXBeanMock = PowerMockito.mock(AgentManagerJMXMBean.class);
        PowerMockito.mockStatic(AgentManagerJMXClient.class);
        PowerMockito.when(AgentManagerJMXClient.getMBeanProxy()).thenReturn(agentManagerJMXBeanMock);
        
        EnhancedManagementConsole console = new EnhancedManagementConsole();
        console.handleCommandLine("start AgentName");
        
        Mockito.verify(agentManagerJMXBeanMock, Mockito.times(1)).startAgent("AgentName");
        
    }

}
