package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.test.context.junit4.SpringRunner;

import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

/**
 * Unit tests for the {@link AgentsCommand} class
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(SpringRunner.class)
public class AgentsCommandTest
{
    // Test data
    private static final String TIMER = "TIMER";
    private static final String DAEMON = "DAEMON";

    private static final AgentDTO AGENT1 = new AgentDTO("name1", DAEMON, "RUNNING", false);
    private static final AgentDTO AGENT2 = new AgentDTO("name2", TIMER, "SET", false);
    private static final AgentDTO AGENT3 = new AgentDTO("name3", TIMER, "STARTED", false);
    private static final AgentDTO HIDDEN_AGENT = new AgentDTO("name4", TIMER, "STARTED", true);

    private static final List<AgentDTO> ALL_AGENTS_LIST = Arrays.asList(AGENT1, AGENT2, AGENT3, HIDDEN_AGENT);

    private static final String AGENT1_EXPECTED_STR_COMP = "name1" + DAEMON + "RUNNING";
    private static final String AGENT2_EXPECTED_STR_COMP = "name2" + TIMER + "SET";
    private static final String AGENT3_EXPECTED_STR_COMP = "name3" + TIMER + "STARTED";
    private static final String HIDDEN_AGENT_EXPECTED_STR_COMP = "name4" + TIMER + "STARTED";

    private StringWriter sw = new StringWriter();

    @Mock
    private AgentManagerJMXMBean jmx;
    @Mock
    private AgentManagerJMXClient client;
    @Spy
    private Commands parent = new Commands(new PrintWriter(sw));

    @InjectMocks
    private AgentsCommand command;

    @Before
    public void setup() throws IOException
    {
        PowerMockito.when(client.getMBeanProxy()).thenReturn(jmx);
    }

    private void expectAllAgents()
    {
        when(jmx.getAgentDTOs()).thenReturn(ALL_AGENTS_LIST);
    }

    @Test
    public void testListAgents() throws IOException
    {
        expectAllAgents();
        command.run();

        // Trim variable padding spaces for testing
        String out = sw.toString().replace(" ", "");
        assertTrue(out.contains(AGENT1_EXPECTED_STR_COMP));
        assertTrue(out.contains(AGENT2_EXPECTED_STR_COMP));
        assertTrue(out.contains(AGENT3_EXPECTED_STR_COMP));
        assertFalse("The hidden agent should not be displayed", out.contains(HIDDEN_AGENT_EXPECTED_STR_COMP));
    }
    
    @Test
    public void testListAllAgents() throws IOException
    {
        expectAllAgents();
        command.setAll(true);
        command.run();

        // Trim variable padding spaces for testing
        String out = sw.toString().replace(" ", "");
        assertTrue(out.contains(AGENT1_EXPECTED_STR_COMP));
        assertTrue(out.contains(AGENT2_EXPECTED_STR_COMP));
        assertTrue(out.contains(AGENT3_EXPECTED_STR_COMP));
        assertTrue("The hidden agent should be displayed", out.contains(HIDDEN_AGENT_EXPECTED_STR_COMP));
    }

    @Test
    public void testListDaemonAgentOnly() throws IOException
    {
        expectAllAgents();
        command.setType("daemon");
        command.run();

        // Trim variable padding spaces for testing
        String out = sw.toString().replace(" ", "");
        assertTrue("The deamon agent should have been printed", out.contains(AGENT1_EXPECTED_STR_COMP));
        assertFalse("The timer agent should not have been printed", out.contains(AGENT2_EXPECTED_STR_COMP));
        assertFalse("The timer agent should not have been printed", out.contains(AGENT3_EXPECTED_STR_COMP));
        assertFalse("The hidden agent should not be displayed", out.contains(HIDDEN_AGENT_EXPECTED_STR_COMP));
    }

    @Test
    public void testListTimerAgentOnly() throws IOException
    {
        expectAllAgents();
        command.setType("timer");
        command.run();

        // Trim variable padding spaces for testing
        String out = sw.toString().replace(" ", "");
        assertFalse("The deamon agent should not have been printed", out.contains(AGENT1_EXPECTED_STR_COMP));
        assertTrue("The timer agent should have been printed", out.contains(AGENT2_EXPECTED_STR_COMP));
        assertTrue("The timer agent should have been printed", out.contains(AGENT3_EXPECTED_STR_COMP));
        assertFalse("The hidden agent should not be displayed", out.contains(HIDDEN_AGENT_EXPECTED_STR_COMP));
    }

    @Test
    public void testListNoAgent() throws IOException
    {
        when(jmx.getAgentDTOs()).thenReturn(Collections.emptyList());
        command.run();

        assertTrue(sw.toString().contains("No agent found"));
    }

}
