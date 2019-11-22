package net.obvj.smart.manager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.agents.api.Agent.State;
import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.conf.AgentsXml;
import net.obvj.smart.conf.xml.AgentConfiguration;

/**
 * Unit tests for the {@link AgentManager} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentManagerTest
{
    // Test data
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String DUMMY_DAEMON = "DummyDaemon";
    private static final String AGENT1 = "agent1";
    private static final String UNKNOWN = "Unknown";

    private static final String TIMER = "timer";
    private static final String DAEMON = "daemon";

    private static final List<String> names = Arrays.asList(DUMMY_AGENT, DUMMY_DAEMON);

    private static final AgentConfiguration XML_DUMMY_AGENT = new AgentConfiguration.Builder(DUMMY_AGENT).type(TIMER)
            .agentClass("net.obvj.smart.agents.dummy.DummyAgent").interval("1 hour").build();
    private static final AgentConfiguration XML_DUMMY_DAEMON = new AgentConfiguration.Builder(DUMMY_DAEMON).type(DAEMON)
            .agentClass("net.obvj.smart.agents.dummy.DummyDaemonAgent").build();

    private static final AgentDTO DUMMY_AGENT_DTO = new AgentDTO(DUMMY_AGENT, TIMER, "SET");
    private static final AgentDTO DUMMY_DAEMON_DTO = new AgentDTO(DUMMY_DAEMON, DAEMON, "SET");
    
    private static final List<AgentDTO> ALL_AGENT_DTOS = Arrays.asList(DUMMY_AGENT_DTO, DUMMY_DAEMON_DTO);
    
    private Agent dummyAgent;
    private Agent dummyDaemonAgent;
    private Agent[] allAgents;
    
    @Mock
    private AgentsXml agentsXml;
    @InjectMocks
    private AgentManager manager;

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        dummyAgent = Agent.parseAgent(XML_DUMMY_AGENT);
        dummyDaemonAgent = Agent.parseAgent(XML_DUMMY_DAEMON);
        allAgents = new Agent[] { dummyAgent, dummyDaemonAgent };
    }

    protected void prepareAgentManager(Agent... agents)
    {
        Arrays.stream(agents).forEach(manager::addAgent);
    }

    @Test
    public void testGetAgentNamesWithOneAgent()
    {
        prepareAgentManager(dummyAgent);
        String[] agentNames = manager.getAgentNames();
        assertEquals(1, agentNames.length);
        assertEquals(dummyAgent.getName(), agentNames[0]);
    }

    @Test
    public void testGetAgents()
    {
        prepareAgentManager(allAgents);
        Collection<Agent> agents = manager.getAgents();
        assertEquals(2, agents.size());
        List<String> managerNames = agents.stream().map(Agent::getName).collect(Collectors.toList());
        assertTrue(managerNames.containsAll(names));
    }

    @Test
    public void testFindAgentByName()
    {
        prepareAgentManager(allAgents);
        assertEquals(dummyAgent, manager.findAgentByName(DUMMY_AGENT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAgentByNameUnknown()
    {
        manager.findAgentByName(UNKNOWN);
    }

    @Test
    public void testStartTimerAgentWithPreviousStateSet()
    {
        prepareAgentManager(dummyAgent);
        assertEquals(State.SET, dummyAgent.getState());
        manager.startAgent(DUMMY_AGENT);
        Awaitility.await().until(dummyAgent::isStarted);
        assertNotNull(dummyAgent.getStartDate());
        assertTrue(manager.isAgentStarted(DUMMY_AGENT));
    }

    @Test
    public void testRemoveAgent()
    {
        prepareAgentManager(allAgents);
        manager.removeAgent(DUMMY_AGENT);
        assertEquals(1, manager.getAgents().size());
        assertFalse(manager.getAgents().contains(dummyAgent));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveAgentUnknown()
    {
        manager.removeAgent(UNKNOWN);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveAgentStarted()
    {
        prepareAgentManager(dummyAgent);
        manager.startAgent(DUMMY_AGENT);
        manager.removeAgent(DUMMY_AGENT);
    }
    
    @Test
    public void testGetAgentDTOs()
    {
        prepareAgentManager(allAgents);
        Collection<AgentDTO> dtos = manager.getAgentDTOs();
        assertEquals(2, dtos.size());
        assertTrue(dtos.containsAll(ALL_AGENT_DTOS));
    }
    
    @Test
    public void testGetAgentStatusStr()
    {
        Agent agent = mock(Agent.class);
        when(agent.getName()).thenReturn(AGENT1);
        when(agent.getStatusString()).thenReturn("statusStr1");
        prepareAgentManager(agent);
        assertEquals("statusStr1", manager.getAgentStatusStr(AGENT1));
    }

    @Test
    public void testStopTimerAgentWithPreviousStateStarted() throws TimeoutException
    {
        prepareAgentManager(dummyAgent);
        manager.startAgent(DUMMY_AGENT);
        Awaitility.await().until(dummyAgent::isStarted);
        manager.stopAgent(DUMMY_AGENT);
        Awaitility.await().until(dummyAgent::isStopped);
    }
    
    @Test
    public void testRunTimerAgent()
    {
        Agent agent = mock(Agent.class);
        when(agent.getName()).thenReturn(AGENT1);
        when(agent.getType()).thenReturn(TIMER);
        prepareAgentManager(agent);
        manager.runNow(AGENT1);
        verify(agent).run();
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testRunDaemonAgent()
    {
        prepareAgentManager(dummyDaemonAgent);
        manager.runNow(DUMMY_DAEMON);
    }
    
    @Test
    public void testResetAgentWithPreviousStateSet() throws ReflectiveOperationException
    {
        when(agentsXml.getAgentConfiguration(DUMMY_AGENT)).thenReturn(XML_DUMMY_AGENT);

        prepareAgentManager(dummyAgent);
        manager.resetAgent(DUMMY_AGENT);
        Agent newAgent = manager.findAgentByName(DUMMY_AGENT);
        
        // A new Agent instance is available
        assertNotSame(dummyAgent, newAgent);
        
        assertEquals(dummyAgent.getName(), newAgent.getName());
        assertEquals(dummyAgent.getType(), newAgent.getType());
        assertEquals(dummyAgent.getClass(), newAgent.getClass());
        assertEquals(dummyAgent.getStopTimeoutSeconds(), newAgent.getStopTimeoutSeconds());
        
        assertEquals(State.SET, newAgent.getState());
    }
    
    @Test(expected = IllegalStateException.class)
    public void testResetAgentWithPreviousStateStarted() throws ReflectiveOperationException
    {
        prepareAgentManager(dummyAgent);
        manager.startAgent(DUMMY_AGENT);
        manager.resetAgent(DUMMY_AGENT);
    }
    
    @Test
    public void testIsAgentRunning()
    {
        Agent agent = mock(Agent.class);
        when(agent.isRunning()).thenReturn(true);
        when(agent.getName()).thenReturn("agent1");
        prepareAgentManager(agent);
        assertTrue(manager.isAgentRunning("agent1"));
    }
    
}
