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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.Agent;
import net.obvj.smart.agents.AgentFactory;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.conf.AgentLoader;
import net.obvj.smart.conf.properties.SmartProperties;
import net.obvj.smart.console.ManagementConsole;
import net.obvj.smart.jmx.JMXException;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.ApplicationContextFacade;

/**
 * Unit tests for operations inside {@link SmartServerSupport}, with mocks.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*" })
@PrepareForTest({ ApplicationContextFacade.class, Agent.class })
public class SmartServerSupportTest
{
    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder("timer")
            .name("DummyAgent").agentClass("net.obvj.smart.agents.dummy.DummyAgent").frequency("3 hours")
            .automaticallyStarted(false).build();

    private static final AgentConfiguration DUMMY_AGENT_CONFIG_AUTO = new AgentConfiguration.Builder("timer")
            .name("DummyAgentAuto").agentClass("net.obvj.smart.agents.dummy.DummyAgent").frequency("3 hours")
            .automaticallyStarted(true).build();

    @Mock
    private SmartProperties properties;
    @Mock
    private ManagementConsole console;
    @Mock
    private AgentLoader agentLoader;;
    @Mock
    private AgentManager manager;

    // Agents
    private Agent dummyAgent;
    private Agent dummyAgentAuto;
    private List<Agent> allAgents;

    // Test subject
    private SmartServerSupport support;

    @Before
    public void setup() throws ReflectiveOperationException
    {
        mockStatic(ApplicationContextFacade.class);
        when(ApplicationContextFacade.getBean(ManagementConsole.class)).thenReturn(console);
        when(ApplicationContextFacade.getBean(AgentManager.class)).thenReturn(manager);
        when(ApplicationContextFacade.getBean(AgentLoader.class)).thenReturn(agentLoader);
        when(ApplicationContextFacade.getBean(SmartProperties.class)).thenReturn(properties);

        // Setup agents
        dummyAgent = spy(AgentFactory.create(DUMMY_AGENT_CONFIG));
        dummyAgentAuto = spy(AgentFactory.create(DUMMY_AGENT_CONFIG_AUTO));
        allAgents = Arrays.asList(dummyAgent, dummyAgentAuto);

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
    public void testStartAutomaticAgents()
    {
        when(manager.getAgents()).thenReturn(allAgents);
        support.startAutomaticAgents();
        verify(manager, never()).startAgent("DummyAgent");
        verify(manager).startAgent("DummyAgentAuto");
    }

    @Test(expected = JMXException.class)
    public void testRegisterManagedBeanWithException()
    {
        when(properties.getProperty(SmartProperties.JMX_AGENT_MANAGER_OBJECT_NAME)).thenReturn("name1");
        support.registerManagedBean();
    }

}
