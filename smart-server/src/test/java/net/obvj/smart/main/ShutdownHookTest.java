package net.obvj.smart.main;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.Agent;
import net.obvj.smart.conf.properties.SmartProperties;
import net.obvj.smart.console.ManagementConsole;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.ApplicationContextFacade;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*" })
@PrepareForTest({ ApplicationContextFacade.class, Agent.class })
public class ShutdownHookTest
{
    @Mock
    private SmartProperties properties;
    @Mock
    private ManagementConsole console;
    @Mock
    private AgentManager manager;
    @Mock
    private Agent agent1;
    @Mock
    private Agent agent2;

    // Test subject
    private ShutdownHook hook;

    @Before
    public void setup()
    {
        mockStatic(ApplicationContextFacade.class);
        when(ApplicationContextFacade.getBean(ManagementConsole.class)).thenReturn(console);
        when(ApplicationContextFacade.getBean(AgentManager.class)).thenReturn(manager);
        when(ApplicationContextFacade.getBean(SmartProperties.class)).thenReturn(properties);

        hook = new ShutdownHook();
    }

    @Test
    public void testWithTwoAgentsAndClasicConsoleEnabled() throws TimeoutException
    {
        when(properties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED)).thenReturn(true);
        when(manager.getAgents()).thenReturn(Arrays.asList(agent1, agent2));

        hook.run();

        // Check that both agents were stopped
        verify(agent1).stop();
        verify(agent2).stop();

        // Check that the classic console was closed
        verify(console).stop();
    }

    @Test
    public void testWithOneAgentAndClasicConsoleDisabled() throws TimeoutException
    {
        when(properties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED)).thenReturn(false);
        when(manager.getAgents()).thenReturn(Arrays.asList(agent1));

        hook.run();

        // Check that the agent was stopped
        verify(agent1).stop();

        // Check that the classic console was not closed
        verify(console, never()).stop();
    }

}
