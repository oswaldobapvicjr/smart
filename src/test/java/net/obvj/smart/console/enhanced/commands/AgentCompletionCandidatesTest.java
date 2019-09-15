package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AgentManagerJMXClient.class)
public class AgentCompletionCandidatesTest
{
    private static final String[] ALL_AGENTS = new String[] { "agent1", "agent2" };

    private AgentManagerJMXMBean agentManagerJMXBeanMock = PowerMockito.mock(AgentManagerJMXMBean.class);

    @Before
    public void setup()
    {
        PowerMockito.mockStatic(AgentManagerJMXClient.class);
    }

    @Test
    public void testRetrieveAgentNames()
    {
        PowerMockito.when(AgentManagerJMXClient.getMBeanProxy()).thenReturn(agentManagerJMXBeanMock);
        PowerMockito.when(agentManagerJMXBeanMock.getAgentNames()).thenReturn(ALL_AGENTS);
        AgentCompletionCandidates candidates = new AgentCompletionCandidates();
        assertTrue(candidates.containsAll(Arrays.asList(ALL_AGENTS)));
    }

    @Test
    public void testUnableToRetrieveAgentNames()
    {
        PowerMockito.when(AgentManagerJMXClient.getMBeanProxy()).thenReturn(null);
        AgentCompletionCandidates candidates = new AgentCompletionCandidates();
        System.out.println(candidates);
        assertTrue(candidates.isEmpty());
    }

}
