package net.obvj.smart.console.enhanced.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
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
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;

/**
 * Unit tests for the {@link AgentsCommand} class
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AgentManagerJMXClient.class)
public class AgentsCommandTest
{
    // Test data
    private static final String TIMER = "TIMER";
    private static final String DAEMON = "DAEMON";

    private static final AgentDTO AGENT1 = new AgentDTO("name1", DAEMON, "RUNNING");
    private static final AgentDTO AGENT2 = new AgentDTO("name2", TIMER, "SET");
    private static final AgentDTO AGENTS3 = new AgentDTO("name3", TIMER, "STARTED");

    private static final List<AgentDTO> ALL_AGENTS_LIST = Arrays.asList(AGENT1, AGENT2, AGENTS3);

    private static final String AGENT1_EXPECTED_STR_COMP = "name1" + DAEMON + "RUNNING";
    private static final String AGENT2_EXPECTED_STR_COMP = "name2" + TIMER + "SET";
    private static final String AGENT3_EXPECTED_STR_COMP = "name3" + TIMER + "STARTED";

    @Mock
    private AgentManagerJMXMBean agentManagerJMXBean;

    @Before
    public void setup() throws IOException
    {
        mockStatic(AgentManagerJMXClient.class);
        when(AgentManagerJMXClient.getMBeanProxy()).thenReturn(agentManagerJMXBean);
    }

    private void expectAllAgents()
    {
        when(agentManagerJMXBean.getAgentDTOs()).thenReturn(ALL_AGENTS_LIST);
    }

    /**
     * Creates a new command that will print its output onto the given StringWriter.
     * 
     * @param out the StringWriter to which the command will print
     * @return a {@link AgentsCommand} for testing
     */
    private AgentsCommand newCommandWithOutput(StringWriter out)
    {
        AgentsCommand command = new AgentsCommand();
        command.setParent(new Commands(new PrintWriter(out)));
        return command;
    }

    @Test
    public void testListAllAgents() throws IOException
    {
        expectAllAgents();
        StringWriter sw = new StringWriter();
        AgentsCommand command = newCommandWithOutput(sw);
        command.run();

        // Trim variable padding spaces for testing
        String out = sw.toString().replace(" ", "");
        assertTrue(out.contains(AGENT1_EXPECTED_STR_COMP));
        assertTrue(out.contains(AGENT2_EXPECTED_STR_COMP));
        assertTrue(out.contains(AGENT3_EXPECTED_STR_COMP));
    }

    @Test
    public void testListDaemonAgentOnly() throws IOException
    {
        expectAllAgents();
        StringWriter sw = new StringWriter();
        AgentsCommand command = newCommandWithOutput(sw);
        command.setType("daemon");
        command.run();

        // Trim variable padding spaces for testing
        String out = sw.toString().replace(" ", "");
        assertTrue("The deamon agent should have been printed", out.contains(AGENT1_EXPECTED_STR_COMP));
        assertFalse("The timer agent should not have been printed", out.contains(AGENT2_EXPECTED_STR_COMP));
        assertFalse("The timer agent should not have been printed", out.contains(AGENT3_EXPECTED_STR_COMP));
    }

    @Test
    public void testListTimerAgentOnly() throws IOException
    {
        expectAllAgents();
        StringWriter sw = new StringWriter();
        AgentsCommand command = newCommandWithOutput(sw);
        command.setType("timer");
        command.run();

        // Trim variable padding spaces for testing
        String out = sw.toString().replace(" ", "");
        assertFalse("The deamon agent should not have been printed", out.contains(AGENT1_EXPECTED_STR_COMP));
        assertTrue("The timer agent should have been printed", out.contains(AGENT2_EXPECTED_STR_COMP));
        assertTrue("The timer agent should have been printed", out.contains(AGENT3_EXPECTED_STR_COMP));
    }

    @Test
    public void testListNoAgent() throws IOException
    {
        when(agentManagerJMXBean.getAgentDTOs()).thenReturn(Collections.emptyList());

        StringWriter sw = new StringWriter();
        AgentsCommand command = newCommandWithOutput(sw);
        command.run();

        assertTrue(sw.toString().contains("No agent found"));
    }

}
