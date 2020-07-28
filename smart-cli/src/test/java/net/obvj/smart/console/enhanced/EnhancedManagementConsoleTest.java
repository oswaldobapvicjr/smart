package net.obvj.smart.console.enhanced;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import jline.console.ConsoleReader;
import net.obvj.smart.conf.properties.SmartProperties;
import net.obvj.smart.console.enhanced.EnhancedManagementConsole.Mode;
import net.obvj.smart.console.enhanced.commands.AgentCompletionCandidates;
import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.util.ClientApplicationContextFacade;
import net.obvj.smart.util.ConsoleUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ConsoleUtils.class, ClientApplicationContextFacade.class, AgentCompletionCandidates.class })
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.*" })
public class EnhancedManagementConsoleTest
{
    @Mock
    private AgentManagerJMXMBean agentManagerJMXBean;
    @Mock
    private AgentManagerJMXClient agentManagerJMXClient;
    @Mock
    private SmartProperties smartProperties;
    @Mock
    private ConsoleReader reader;

    @InjectMocks
    private EnhancedManagementConsole console;

    private StringWriter out = new StringWriter();

    @Before
    public void setup()
    {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(ConsoleUtils.class);
        PowerMockito.mockStatic(ClientApplicationContextFacade.class);
        PowerMockito.mockStatic(AgentCompletionCandidates.class);

        PowerMockito.when(ClientApplicationContextFacade.getBean(SmartProperties.class)).thenReturn(smartProperties);
        PowerMockito.when(ClientApplicationContextFacade.getBean(AgentManagerJMXClient.class)).thenReturn(agentManagerJMXClient);
        PowerMockito.when(agentManagerJMXClient.getMBeanProxy()).thenReturn(agentManagerJMXBean);
        PowerMockito.when(agentManagerJMXBean.getAgentNames()).thenReturn(new String[] { "agent1", "agent2" });

        Mockito.when(reader.getOutput()).thenReturn(out);
    }

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
        PowerMockito.when(ConsoleUtils.readCustomHeaderLines())
                .thenReturn(Arrays.asList("Header line 1", "Header line 2"));
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

        PowerMockito.when(ConsoleUtils.readCustomHeaderLines())
                .thenReturn(Arrays.asList("Header line 1", "Header line 2"));
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
        console.handleCommandLine("start AgentName");
        Mockito.verify(agentManagerJMXBean, Mockito.times(1)).startAgent("AgentName");
    }

    @Test
    public void testHandleHelpCommandLine() throws IOException
    {
        console.handleCommandLine("help");
        Mockito.verify(reader).println(ArgumentMatchers.contains("Available commands:"));
    }

    @Test
    public void testRunOnSingleCommandMode() throws IOException
    {
        new EnhancedManagementConsole("start AgentName").run();
        Mockito.verify(agentManagerJMXBean, Mockito.times(1)).startAgent("AgentName");
    }

    @Test
    public void testRunOnInteractiveModeWithExitCommand() throws IOException
    {
        Mockito.when(reader.readLine()).thenReturn("exit");
        console.run();
        Mockito.verifyZeroInteractions(agentManagerJMXBean);
    }

    @Test
    public void testRunOnInteractiveModeWithQuitCommand() throws IOException
    {
        Mockito.when(reader.readLine()).thenReturn("quit");
        console.run();
        Mockito.verifyZeroInteractions(agentManagerJMXBean);
    }

}
