package net.obvj.smart.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.agents.api.Agent.State;
import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.conf.xml.XmlAgent;

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
    private static final String UNKNOWN = "Unknown";
    private static final String DUMMY_DAEMON = "DummyDaemon";

    private static final List<String> names = Arrays.asList(DUMMY_AGENT, DUMMY_DAEMON);

    private static final XmlAgent XML_DUMMY_AGENT = new XmlAgent.Builder(DUMMY_AGENT).type("timer")
            .agentClass("net.obvj.smart.agents.dummy.DummyAgent").interval("1 hour").build();
    private static final XmlAgent XML_DUMMY_DAEMON = new XmlAgent.Builder(DUMMY_DAEMON).type("daemon")
            .agentClass("net.obvj.smart.agents.dummy.DummyDaemonAgent").build();

    private static final AgentDTO DUMMY_AGENT_DTO = new AgentDTO(DUMMY_AGENT, "timer", "SET");
    private static final AgentDTO DUMMY_DAEMON_DTO = new AgentDTO(DUMMY_DAEMON, "daemon", "SET");
    
    private static final List<AgentDTO> ALL_AGENT_DTOS = Arrays.asList(DUMMY_AGENT_DTO, DUMMY_DAEMON_DTO);
    
    private Agent dummyAgent;
    private Agent dummyDaemonAgent;
    private Agent[] allAgents;

    @Before
    public void setup() throws Exception
    {
        dummyAgent = Agent.parseAgent(XML_DUMMY_AGENT);
        dummyDaemonAgent = Agent.parseAgent(XML_DUMMY_DAEMON);
        allAgents = new Agent[] { dummyAgent, dummyDaemonAgent };
    }

    protected AgentManager newAgentManager(Agent... agents)
    {
        AgentManager manager = new AgentManager();
        Arrays.stream(agents).forEach(manager::addAgent);
        return manager;
    }

    @Test
    public void testGetAgentNamesWithOneAgent()
    {
        AgentManager manager = newAgentManager(dummyAgent);
        String[] agentNames = manager.getAgentNames();
        assertEquals(1, agentNames.length);
        assertEquals(dummyAgent.getName(), agentNames[0]);
    }

    @Test
    public void testGetAgents()
    {
        AgentManager manager = newAgentManager(allAgents);
        Collection<Agent> agents = manager.getAgents();
        assertEquals(2, agents.size());
        List<String> managerNames = agents.stream().map(Agent::getName).collect(Collectors.toList());
        assertTrue(managerNames.containsAll(names));
    }

    @Test
    public void testFindAgentByName()
    {
        AgentManager manager = newAgentManager(allAgents);
        assertEquals(dummyAgent, manager.findAgentByName(DUMMY_AGENT));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAgentByNameUnknown()
    {
        newAgentManager().findAgentByName(UNKNOWN);
    }

    @Test
    public void testStartTimerAgentWithPreviousStateSet()
    {
        AgentManager manager = newAgentManager(dummyAgent);
        assertEquals(State.SET, dummyAgent.getState());
        manager.startAgent(DUMMY_AGENT);
        Awaitility.await().until(dummyAgent::isStarted);
        assertNotNull(dummyAgent.getStartDate());
        assertTrue(manager.isAgentStarted(DUMMY_AGENT));
    }

    @Test
    public void testStartDaemonAgentWithPreviousStateSet()
    {
        AgentManager manager = newAgentManager(dummyDaemonAgent);
        assertEquals(State.SET, dummyDaemonAgent.getState());
        manager.startAgent(DUMMY_DAEMON);
        Awaitility.await().until(dummyDaemonAgent::isStarted);
        assertNotNull(dummyDaemonAgent.getStartDate());
        assertTrue(manager.isAgentStarted(DUMMY_DAEMON));
        assertTrue(manager.isAgentRunning(DUMMY_DAEMON));
    }

    @Test
    public void testRemoveAgent()
    {
        AgentManager manager = newAgentManager(allAgents);
        manager.removeAgent(DUMMY_AGENT);
        assertEquals(1, manager.getAgents().size());
        assertFalse(manager.getAgents().contains(dummyAgent));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveAgentUnknown()
    {
        newAgentManager().removeAgent(UNKNOWN);
    }

    @Test(expected = IllegalStateException.class)
    public void testRemoveAgentStarted()
    {
        AgentManager manager = newAgentManager(dummyAgent);
        manager.startAgent(DUMMY_AGENT);
        manager.removeAgent(DUMMY_AGENT);
    }
    
    @Test
    public void testGetAgentDTOs()
    {
        AgentManager manager = newAgentManager(allAgents);
        Collection<AgentDTO> dtos = manager.getAgentDTOs();
        assertEquals(2, dtos.size());
        assertTrue(dtos.containsAll(ALL_AGENT_DTOS));
    }
    
    @Test
    public void testGetAgentStatusStr()
    {
        Agent agent = Mockito.mock(Agent.class);
        Mockito.when(agent.getName()).thenReturn("agent1");
        Mockito.when(agent.getStatusString()).thenReturn("statusStr1");
        AgentManager manager = newAgentManager(agent);
        assertEquals("statusStr1", manager.getAgentStatusStr("agent1"));
    }

    @Test
    public void testStopTimerAgentWithPreviousStateStarted() throws TimeoutException
    {
        AgentManager manager = newAgentManager(dummyAgent);
        manager.startAgent(DUMMY_AGENT);
        Awaitility.await().until(dummyAgent::isStarted);
        manager.stopAgent(DUMMY_AGENT);
        Awaitility.await().until(dummyAgent::isStopped);
    }
    
    @Test
    public void testRunTimerAgent()
    {
        Agent agent = Mockito.mock(Agent.class);
        Mockito.when(agent.getName()).thenReturn("agent1");
        Mockito.when(agent.getType()).thenReturn("timer");
        AgentManager manager = newAgentManager(agent);
        manager.runNow("agent1");
        Mockito.verify(agent).run();
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testRunDaemonAgent()
    {
        AgentManager manager = newAgentManager(dummyDaemonAgent);
        manager.runNow(DUMMY_DAEMON);
    }
    
}
