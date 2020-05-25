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
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.junit.utils.TestUtils;
import net.obvj.smart.agents.Agent;
import net.obvj.smart.agents.Agent.State;
import net.obvj.smart.agents.AgentFactory;
import net.obvj.smart.agents.dto.AgentDTO;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.conf.AgentLoader;

/**
 * Unit tests for the {@link AgentManager} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AgentFactory.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.*" })
public class AgentManagerTest
{
    // Test data
    private static final String DUMMY_AGENT = "DummyAgent";
    private static final String HIDDEN_AGENT = "HiddenAgent";
    private static final String DUMMY_AGENT_CLASS = "net.obvj.smart.agents.dummy.DummyAgent";
    private static final String AGENT1 = "agent1";
    private static final String UNKNOWN = "Unknown";

    private static final String TIMER = "timer";

    private static final List<String> names = Arrays.asList(DUMMY_AGENT);

    private static final AgentConfiguration XML_DUMMY_AGENT = new AgentConfiguration.Builder(TIMER).name(DUMMY_AGENT)
            .agentClass(DUMMY_AGENT_CLASS).frequency("1 hour").build();
    private static final AgentConfiguration XML_HIDDEN_AGENT = new AgentConfiguration.Builder(TIMER).name(HIDDEN_AGENT)
            .agentClass(DUMMY_AGENT_CLASS).frequency("1 hour").hidden(true).build();

    private static final AgentDTO DUMMY_AGENT_DTO = new AgentDTO(DUMMY_AGENT, TIMER, "SET", false);

    private static final List<AgentConfiguration> ALL_AGENT_CONFIGS = Arrays.asList(XML_DUMMY_AGENT);
    private static final List<AgentDTO> ALL_AGENT_DTOS = Arrays.asList(DUMMY_AGENT_DTO);

    private Agent dummyAgent;
    private Agent hiddenAgent;

    private Agent[] allPublicAgents;

    @Mock
    private AgentLoader agentLoader;
    @InjectMocks
    private AgentManager manager;

    @Before
    public void setup() throws Exception
    {
        MockitoAnnotations.initMocks(this);

        dummyAgent = AgentFactory.create(XML_DUMMY_AGENT);
        hiddenAgent = AgentFactory.create(XML_HIDDEN_AGENT);
        allPublicAgents = new Agent[] { dummyAgent };
    }

    protected void prepareAgentManager(Agent... agents)
    {
        Arrays.stream(agents).forEach(manager::addAgent);
    }

    @Test
    public void testGetAgentNamesWithOneAgent()
    {
        prepareAgentManager(dummyAgent);
        String[] agentNames = manager.getPublicAgentNames();
        assertEquals(1, agentNames.length);
        assertEquals(dummyAgent.getName(), agentNames[0]);
    }

    @Test
    public void testGetAgentNamesWithAPublicAgentAndAHiddenOne()
    {
        prepareAgentManager(dummyAgent, hiddenAgent);
        String[] agentNames = manager.getPublicAgentNames();
        assertEquals(1, agentNames.length);
        assertEquals(dummyAgent.getName(), agentNames[0]);
    }

    @Test
    public void testGetAgents()
    {
        prepareAgentManager(allPublicAgents);
        Collection<Agent> agents = manager.getAgents();
        assertEquals(1, agents.size());
        List<String> managerNames = agents.stream().map(Agent::getName).collect(Collectors.toList());
        assertTrue(managerNames.containsAll(names));
    }

    @Test
    public void testFindAgentByName()
    {
        prepareAgentManager(allPublicAgents);
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
        prepareAgentManager(allPublicAgents);
        manager.removeAgent(DUMMY_AGENT);
        assertEquals(0, manager.getAgents().size());
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
        prepareAgentManager(allPublicAgents);
        Collection<AgentDTO> dtos = manager.getAgentDTOs();
        assertEquals(1, dtos.size());
        assertTrue(dtos.containsAll(ALL_AGENT_DTOS));
    }

    @Test
    public void testGetAgentStatusStr()
    {
        Agent agent = mock(Agent.class);
        when(agent.getName()).thenReturn(AGENT1);
        when(agent.getStatusString()).thenReturn("{\"statusStr\":\"statusStr1\"}");
        prepareAgentManager(agent);
        String agentStatusStrWithoutSpaces = manager.getAgentStatusStr(AGENT1).replace(" ", "");
        TestUtils.assertStringContains(agentStatusStrWithoutSpaces, "\"statusStr\":\"statusStr1\"");
    }

    @Test
    public void testGetAgentStatusStrNotPrettyPrinted()
    {
        Agent agent = mock(Agent.class);
        when(agent.getName()).thenReturn(AGENT1);
        when(agent.getStatusString()).thenReturn("statusStr1");
        prepareAgentManager(agent);
        String agentStatusStr = manager.getAgentStatusStr(AGENT1, false);
        assertEquals("statusStr1", agentStatusStr);
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
        verify(agent).run(true);
    }

    @Test
    public void testResetAgentWithPreviousStateSet() throws ReflectiveOperationException
    {
        when(agentLoader.getAgentConfigurationByClass(DUMMY_AGENT_CLASS)).thenReturn(XML_DUMMY_AGENT);

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

    @Test
    public void testLoadAgents() throws ReflectiveOperationException
    {
        PowerMockito.mockStatic(AgentFactory.class);
        PowerMockito.when(AgentFactory.create(XML_DUMMY_AGENT)).thenReturn(dummyAgent);
        when(agentLoader.getAgents()).thenReturn(ALL_AGENT_CONFIGS);

        manager.loadAgents();
        assertEquals(1, manager.getAgents().size());
        assertTrue(manager.getAgents().contains(dummyAgent));
    }

    @Test
    public void testLoadAgentsWithOneException() throws ReflectiveOperationException
    {
        PowerMockito.mockStatic(AgentFactory.class);
        PowerMockito.when(AgentFactory.create(XML_DUMMY_AGENT)).thenThrow(new IllegalStateException());
        when(agentLoader.getAgents()).thenReturn(ALL_AGENT_CONFIGS);

        manager.loadAgents();
        assertEquals(0, manager.getAgents().size());
        assertFalse(manager.getAgents().contains(dummyAgent));
    }

}
