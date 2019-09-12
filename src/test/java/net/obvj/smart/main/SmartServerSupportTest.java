package net.obvj.smart.main;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.conf.SmartProperties;
import net.obvj.smart.conf.xml.AgentConfiguration;
import net.obvj.smart.console.ManagementConsole;
import net.obvj.smart.manager.AgentManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SmartProperties.class, ManagementConsole.class, AgentManager.class })
public class SmartServerSupportTest
{
    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder("DummyAgent")
            .type("timer").agentClass("net.obvj.smart.agents.dummy.DummyAgent").interval("3 hours")
            .automaticallyStarted(false).build();
    private static final AgentConfiguration DUMMY_DAEMON_CONFIG = new AgentConfiguration.Builder("DummyDaemon")
            .type("daemon").agentClass("net.obvj.smart.agents.dummy.DummyDaemonAgent").automaticallyStarted(true)
            .build();

    // Mock objects
    private SmartProperties properties;
    private ManagementConsole console;
    private AgentManager manager;

    // Agents
    private Agent dummyAgent;
    private Agent dummyDaemon;
    private List<Agent> allAgents;

    // Test subject
    private SmartServerSupport support = new SmartServerSupport();

    @Before
    public void setup() throws ReflectiveOperationException
    {
        // Setup singletons
        properties = mock(SmartProperties.class);
        mockStatic(SmartProperties.class);
        when(SmartProperties.getInstance()).thenReturn(properties);

        console = mock(ManagementConsole.class);
        mockStatic(ManagementConsole.class);
        when(ManagementConsole.getInstance()).thenReturn(console);

        manager = mock(AgentManager.class);
        mockStatic(AgentManager.class);
        when(AgentManager.getInstance()).thenReturn(manager);

        // Setup agents
        dummyAgent = spy(Agent.parseAgent(DUMMY_AGENT_CONFIG));
        dummyDaemon = spy(Agent.parseAgent(DUMMY_DAEMON_CONFIG));
        allAgents = Arrays.asList(dummyAgent, dummyDaemon);

    }

    @Test
    public void testStartClassicManagementConsoleEnabled()
    {
        when(properties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED)).thenReturn(true);
        support.startClassicManagementConsole();
        verify(console).start();
    }

    @Test
    public void testStartClassicManagementConsoleDisabled()
    {
        when(properties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED)).thenReturn(false);
        support.startClassicManagementConsole();
        verify(console, never()).start();
    }

    @Test
    public void testCloseClassicManagementConsoleEnabled()
    {
        when(properties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED)).thenReturn(true);
        support.closeClassicManagementConsole();
        verify(console).stop();
    }

    @Test
    public void testCloseClassicManagementConsoleDisabled()
    {
        when(properties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED)).thenReturn(false);
        support.closeClassicManagementConsole();
        verify(console, never()).stop();
    }

    @Test
    public void testStartAutomaticAgent()
    {
        when(manager.getAgents()).thenReturn(allAgents);
        support.startAutomaticAgents();
        verify(manager, never()).startAgent("DummyAgent");
        verify(manager).startAgent("DummyDaemon");
    }

}
