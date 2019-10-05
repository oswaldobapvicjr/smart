package net.obvj.smart.manager;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
@RunWith(PowerMockRunner.class)
@PrepareForTest(AgentsXml.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.dom.*"})
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
        dummyDaemonAgent = Mockito.spy(dummyDaemonAgent);
        AgentManager manager = newAgentManager(dummyDaemonAgent);
        assertEquals(State.SET, dummyDaemonAgent.getState());
        manager.startAgent(DUMMY_DAEMON);
        Mockito.verify(dummyDaemonAgent).run();
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
        Mockito.when(agent.getName()).thenReturn(AGENT1);
        Mockito.when(agent.getStatusString()).thenReturn("statusStr1");
        AgentManager manager = newAgentManager(agent);
        assertEquals("statusStr1", manager.getAgentStatusStr(AGENT1));
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
        Mockito.when(agent.getName()).thenReturn(AGENT1);
        Mockito.when(agent.getType()).thenReturn(TIMER);
        AgentManager manager = newAgentManager(agent);
        manager.runNow(AGENT1);
        Mockito.verify(agent).run();
    }
    
    @Test(expected = UnsupportedOperationException.class)
    public void testRunDaemonAgent()
    {
        AgentManager manager = newAgentManager(dummyDaemonAgent);
        manager.runNow(DUMMY_DAEMON);
    }
    
    @Test
    public void testResetAgentWithPreviousStateSet() throws ReflectiveOperationException
    {
        // Setup AgentConfiguration mocks
        AgentsXml agentConfigMock = Mockito.mock(AgentsXml.class);
        Mockito.when(agentConfigMock.getAgentConfiguration(DUMMY_AGENT)).thenReturn(XML_DUMMY_AGENT);
        PowerMockito.mockStatic(AgentsXml.class);
        PowerMockito.when(AgentsXml.getInstance()).thenReturn(agentConfigMock);
        
        AgentManager manager = newAgentManager(dummyAgent);
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
        AgentManager manager = newAgentManager(dummyAgent);
        manager.startAgent(DUMMY_AGENT);
        manager.resetAgent(DUMMY_AGENT);
    }
    
}
