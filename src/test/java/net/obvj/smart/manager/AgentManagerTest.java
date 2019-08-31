package net.obvj.smart.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.agents.api.Agent.State;
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

    private static final XmlAgent AML_DUMMY_AGENT = new XmlAgent.Builder(DUMMY_AGENT).type("timer")
            .agentClass("net.obvj.smart.agents.dummy.DummyAgent").interval("30 seconds").build();
    private static final XmlAgent XML_DUMMY_DAEMON = new XmlAgent.Builder(DUMMY_DAEMON).type("daemon")
            .agentClass("net.obvj.smart.agents.dummy.DummyDaemonAgent").build();

    private Agent dummyAgent;
    private Agent dummyDaemonAgent;
    private Agent[] allAgents;

    @Before
    public void setup() throws Exception
    {
        dummyAgent = Agent.parseAgent(AML_DUMMY_AGENT);
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
        AgentManager manager = newAgentManager(dummyAgent, dummyDaemonAgent);
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
        assertEquals(State.STARTED, dummyAgent.getState());
        assertNotNull(dummyAgent.getStartDate());
    }

    @Test
    public void testRemoveAgent()
    {
        AgentManager manager = newAgentManager(dummyAgent, dummyDaemonAgent);
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

}
