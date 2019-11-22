package net.obvj.smart.main;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.conf.AgentConfigurationException;
import net.obvj.smart.conf.AgentsXml;
import net.obvj.smart.conf.SmartProperties;
import net.obvj.smart.conf.xml.AgentConfiguration;
import net.obvj.smart.console.ManagementConsole;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.ApplicationContextFacade;

/**
 * Unit tests for operations inside {@link SmartServerSupport}, with mocks.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContextFacade.class, Agent.class })
public class SmartServerSupportTest
{
    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder("DummyAgent")
            .type("timer").agentClass("net.obvj.smart.agents.dummy.DummyAgent").interval("3 hours")
            .automaticallyStarted(false).build();
    private static final AgentConfiguration DUMMY_DAEMON_CONFIG = new AgentConfiguration.Builder("DummyDaemon")
            .type("daemon").agentClass("net.obvj.smart.agents.dummy.DummyDaemonAgent").automaticallyStarted(true)
            .build();

    private static final List<AgentConfiguration> ALL_AGENT_CONFIGS = Arrays.asList(DUMMY_AGENT_CONFIG,
            DUMMY_DAEMON_CONFIG);

    @Mock
    private SmartProperties properties;
    @Mock
    private ManagementConsole console;
    @Mock
    private AgentsXml agentsXml;;
    @Mock
    private AgentManager manager;

    // Agents
    private Agent dummyAgent;
    private Agent dummyDaemon;
    private List<Agent> allAgents;

    // Test subject
    private SmartServerSupport support;

    @Before
    public void setup() throws ReflectiveOperationException
    {
        mockStatic(ApplicationContextFacade.class);
        when(ApplicationContextFacade.getBean(ManagementConsole.class)).thenReturn(console);
        when(ApplicationContextFacade.getBean(AgentManager.class)).thenReturn(manager);
        when(ApplicationContextFacade.getBean(AgentsXml.class)).thenReturn(agentsXml);
        when(ApplicationContextFacade.getBean(SmartProperties.class)).thenReturn(properties);
        ;

        // Setup agents
        dummyAgent = spy(Agent.parseAgent(DUMMY_AGENT_CONFIG));
        dummyDaemon = spy(Agent.parseAgent(DUMMY_DAEMON_CONFIG));
        allAgents = Arrays.asList(dummyAgent, dummyDaemon);

        // Allow usage of static method parseAgent
        mockStatic(Agent.class);

        support = new SmartServerSupport();
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

    @Test
    public void testLoadAgents() throws ReflectiveOperationException
    {
        when(Agent.parseAgent(DUMMY_AGENT_CONFIG)).thenReturn(dummyAgent);
        when(Agent.parseAgent(DUMMY_DAEMON_CONFIG)).thenReturn(dummyDaemon);
        when(agentsXml.getAgents()).thenReturn(ALL_AGENT_CONFIGS);
        support.loadAgents();
        verify(manager).addAgent(dummyAgent);
        verify(manager).addAgent(dummyDaemon);
    }

    @Test
    public void testLoadAgentsWithOneException() throws ReflectiveOperationException
    {
        when(Agent.parseAgent(DUMMY_AGENT_CONFIG)).thenReturn(dummyAgent);
        when(Agent.parseAgent(DUMMY_DAEMON_CONFIG)).thenThrow(new AgentConfigurationException("dummyException"));
        when(agentsXml.getAgents()).thenReturn(ALL_AGENT_CONFIGS);
        support.loadAgents();
        verify(manager).addAgent(dummyAgent);
        verify(manager, never()).addAgent(dummyDaemon);
    }

}
