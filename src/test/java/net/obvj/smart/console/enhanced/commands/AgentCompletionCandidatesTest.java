package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.util.ApplicationContextFacade;

/**
 * Unit tests for {@link AgentCompletionCandidates}.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(ApplicationContextFacade.class)
public class AgentCompletionCandidatesTest
{
    private static final String[] ALL_AGENTS = new String[] { "agent1", "agent2" };

    @Mock
    private AgentManagerJMXClient agentManagerJMXClient;
    @Mock
    private AgentManagerJMXMBean agentManagerJMXBean;

    @Before
    public void setup()
    {
        PowerMockito.mockStatic(ApplicationContextFacade.class);
        PowerMockito.when(ApplicationContextFacade.getBean(AgentManagerJMXClient.class)).thenReturn(agentManagerJMXClient);
    }

    @Test
    public void testRetrieveAgentNames()
    {
        PowerMockito.when(agentManagerJMXClient.getMBeanProxy()).thenReturn(agentManagerJMXBean);
        PowerMockito.when(agentManagerJMXBean.getAgentNames()).thenReturn(ALL_AGENTS);
        AgentCompletionCandidates candidates = new AgentCompletionCandidates();
        assertTrue(candidates.containsAll(Arrays.asList(ALL_AGENTS)));
    }

    @Test
    public void testUnableToRetrieveAgentNames()
    {
        PowerMockito.when(agentManagerJMXClient.getMBeanProxy()).thenReturn(null);
        AgentCompletionCandidates candidates = new AgentCompletionCandidates();
        System.out.println(candidates);
        assertTrue(candidates.isEmpty());
    }

}
