package net.obvj.smart.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.dto.AgentDTO;
import net.obvj.smart.jmx.dto.ThreadDTO;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.ApplicationContextFacade;
import net.obvj.smart.util.DateUtils;
import net.obvj.smart.util.SystemUtils;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*" })
@PrepareForTest({ ApplicationContextFacade.class, DateUtils.class, SystemUtils.class })
public class CommandTest
{
    private static final String AGENT1 = "Agent1";

    private StringWriter out = new StringWriter();

    @Mock
    private AgentManager manager;

    @Before
    public void setup()
    {
        mockStatic(ApplicationContextFacade.class);
        when(ApplicationContextFacade.getBean(AgentManager.class)).thenReturn(manager);
        mockStatic(DateUtils.class);
        mockStatic(SystemUtils.class);
    }

    private void assertOutputEquals(String expectedString)
    {
        assertEquals(expectedString, out.toString().trim());
    }

    private void assertOutputContains(String... expectedStrings)
    {
        String strOut = out.toString().trim();
        Arrays.stream(expectedStrings)
                .forEach(expectedString -> assertTrue(
                        String.format("Expected string '%s' was not found", expectedString),
                        strOut.contains(expectedString)));
    }

    @Test
    public void testCommandsByNameOrAliasWithValidNames()
    {
        assertEquals(Command.DATE, Command.getByNameOrAlias("date"));
        assertEquals(Command.HELP, Command.getByNameOrAlias("help"));
        assertEquals(Command.RESET, Command.getByNameOrAlias("reset"));
        assertEquals(Command.RUN, Command.getByNameOrAlias("run"));
        assertEquals(Command.SHOW_AGENTS, Command.getByNameOrAlias("show-agents"));
        assertEquals(Command.SHOW_AGENTS, Command.getByNameOrAlias("agents"));
        assertEquals(Command.SHOW_THREADS, Command.getByNameOrAlias("show-threads"));
        assertEquals(Command.SHOW_THREADS, Command.getByNameOrAlias("threads"));
        assertEquals(Command.START, Command.getByNameOrAlias("start"));
        assertEquals(Command.STATUS, Command.getByNameOrAlias("status"));
        assertEquals(Command.STOP, Command.getByNameOrAlias("stop"));
        assertEquals(Command.UPTIME, Command.getByNameOrAlias("uptime"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommandsByNameOrAliasWithInvalidName()
    {
        Command.getByNameOrAlias("x");
    }

    @Test
    public void testCommandsByNameOrAliasOrNullWithInvalidName()
    {
        assertNull(Command.getByNameOrAliasOrNull("x"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommandsByNameOrAliasWithEmptyAlias()
    {
        Command.getByNameOrAlias("");
    }

    @Test
    public void testCommandNameAndAlias()
    {
        assertEquals("show-agents", Command.SHOW_AGENTS.getName());
        assertEquals("agents", Command.SHOW_AGENTS.getAlias());
    }

    @Test
    public void testStartAgent()
    {
        Command.START.execute(new String[] { "start", AGENT1 }, new PrintWriter(out));
        verify(manager).startAgent(AGENT1);
    }

    @Test
    public void testStartAgentMissingParameter()
    {
        Command.START.execute(new String[] { "start" }, new PrintWriter(out));
        assertOutputEquals(Command.MISSING_PARAMETER_AGENT_NAME);
    }

    @Test
    public void testStartAgentInvalidParameter()
    {
        doThrow(new IllegalArgumentException("message1")).when(manager).startAgent("invalidName");
        Command.START.execute(new String[] { "status", "invalidName" }, new PrintWriter(out));
        assertOutputContains("message1");
    }

    @Test
    public void testRunAgent()
    {
        Command.RUN.execute(new String[] { "run", AGENT1 }, new PrintWriter(out));
        verify(manager).runNow(AGENT1);
    }

    @Test
    public void testRunAgentMissingParameter()
    {
        Command.RUN.execute(new String[] { "run" }, new PrintWriter(out));
        assertOutputEquals(Command.MISSING_PARAMETER_AGENT_NAME);
    }

    @Test
    public void testRunAgentInvalidParameter()
    {
        doThrow(new IllegalArgumentException("message1")).when(manager).runNow("invalidName");
        Command.RUN.execute(new String[] { "run", "invalidName" }, new PrintWriter(out));
        assertOutputContains("message1");
    }

    @Test
    public void testStopAgent() throws TimeoutException
    {
        Command.STOP.execute(new String[] { "stop", AGENT1 }, new PrintWriter(out));
        verify(manager).stopAgent(AGENT1);
    }

    @Test
    public void testStopAgentMissingParameter()
    {
        Command.STOP.execute(new String[] { "stop" }, new PrintWriter(out));
        assertOutputEquals(Command.MISSING_PARAMETER_AGENT_NAME);
    }

    @Test
    public void testStopAgentIllegalArgumentException() throws TimeoutException
    {
        doThrow(new IllegalArgumentException("message1")).when(manager).stopAgent("invalidName");
        Command.STOP.execute(new String[] { "stop", "invalidName" }, new PrintWriter(out));
        assertOutputContains("message1");
    }

    @Test
    public void testResetAgent() throws ReflectiveOperationException
    {
        Command.RESET.execute(new String[] { "reset", AGENT1 }, new PrintWriter(out));
        verify(manager).resetAgent(AGENT1);
    }

    @Test
    public void testResetAgentMissingParameter()
    {
        Command.RESET.execute(new String[] { "reset" }, new PrintWriter(out));
        assertOutputEquals(Command.MISSING_PARAMETER_AGENT_NAME);
    }

    @Test
    public void testResetAgentIllegalArgumentException() throws ReflectiveOperationException
    {
        doThrow(new IllegalArgumentException("message1")).when(manager).resetAgent("invalidName");
        Command.RESET.execute(new String[] { "reset", "invalidName" }, new PrintWriter(out));
        assertOutputContains("message1");
    }

    @Test
    public void testStatusAgent()
    {
        when(manager.getAgentStatusStr(AGENT1)).thenReturn("statusAgent1");
        Command.STATUS.execute(new String[] { "status", AGENT1 }, new PrintWriter(out));
        assertOutputEquals("statusAgent1");
    }

    @Test
    public void testStatusAgentMissingParameter()
    {
        Command.STATUS.execute(new String[] { "status" }, new PrintWriter(out));
        assertOutputEquals(Command.MISSING_PARAMETER_AGENT_NAME);
    }

    @Test
    public void testStatusAgentInvalidParameter()
    {
        doThrow(new IllegalArgumentException("message1")).when(manager).getAgentStatusStr("invalidName");
        Command.STATUS.execute(new String[] { "status", "invalidName" }, new PrintWriter(out));
        assertOutputContains("message1");
    }

    @Test
    public void testDate()
    {
        when(DateUtils.now()).thenReturn("date1");
        Command.DATE.execute(null, new PrintWriter(out));
        assertOutputEquals("date1");
    }

    @Test
    public void testUptime()
    {
        when(SystemUtils.getSystemUptime()).thenReturn(987l);
        Command.UPTIME.execute(null, new PrintWriter(out));
        assertOutputEquals("987 milliseconds");
    }

    @Test
    public void testShowThreads()
    {
        ThreadDTO thread1 = new ThreadDTO(1, "name1", "RUNNABLE");
        ThreadDTO thread2 = new ThreadDTO(2, "name2", "WAITING");
        List<ThreadDTO> dtos = Arrays.asList(thread1, thread2);
        when(SystemUtils.getAllSystemTheadsDTOs()).thenReturn(dtos);
        Command.SHOW_THREADS.execute(new String[] { "threads" }, new PrintWriter(out));

        // Trim variable padding spaces for testing
        String out = this.out.toString().replace(" ", "");
        assertTrue(out.contains("1name1RUNNABLE"));
        assertTrue(out.contains("2name2WAITING"));
    }

    @Test
    public void testShowThreadsFilterByName()
    {
        ThreadDTO thread1 = new ThreadDTO(1, "name1", "RUNNABLE");
        ThreadDTO thread2 = new ThreadDTO(2, "name2", "WAITING");
        List<ThreadDTO> dtos = Arrays.asList(thread1, thread2);
        when(SystemUtils.getAllSystemTheadsDTOs()).thenReturn(dtos);
        Command.SHOW_THREADS.execute(new String[] { "threads", "name2" }, new PrintWriter(out));

        // Trim variable padding spaces for testing
        String out = this.out.toString().replace(" ", "");
        assertFalse(out.contains("1name1RUNNABLE"));
        assertTrue(out.contains("2name2WAITING"));
    }

    @Test
    public void testShowThreadsFilterByNameWithWildcard()
    {
        ThreadDTO thread1 = new ThreadDTO(1, "name1", "RUNNABLE");
        ThreadDTO thread2 = new ThreadDTO(2, "name2", "WAITING");
        List<ThreadDTO> dtos = Arrays.asList(thread1, thread2);
        when(SystemUtils.getAllSystemTheadsDTOs()).thenReturn(dtos);
        Command.SHOW_THREADS.execute(new String[] { "threads", "name*" }, new PrintWriter(out));

        // Trim variable padding spaces for testing
        String out = this.out.toString().replace(" ", "");
        assertTrue(out.contains("1name1RUNNABLE"));
        assertTrue(out.contains("2name2WAITING"));
    }

    @Test
    public void testShowThreadsFilterByNameWithWildcardNoMatch()
    {
        ThreadDTO thread1 = new ThreadDTO(1, "name1", "RUNNABLE");
        ThreadDTO thread2 = new ThreadDTO(2, "name2", "WAITING");
        List<ThreadDTO> dtos = Arrays.asList(thread1, thread2);
        when(SystemUtils.getAllSystemTheadsDTOs()).thenReturn(dtos);
        Command.SHOW_THREADS.execute(new String[] { "threads", "invalid*" }, new PrintWriter(out));

        // Trim variable padding spaces for testing
        String out = this.out.toString();
        assertTrue(out.contains("No thread found"));
    }

    @Test
    public void testJavaVersion()
    {
        when(SystemUtils.getJavaVersion()).thenReturn("javaVersion");
        Command.JAVA_VERSION.execute(null, new PrintWriter(out));
        assertOutputEquals("javaVersion");
    }

    @Test
    public void testShowAgents()
    {
        AgentDTO agent1 = new AgentDTO("name1", "TYPE_EXT", "RUNNING", false);
        AgentDTO agent2 = new AgentDTO("name2", "TIMER", "SET", false);
        List<AgentDTO> dtos = Arrays.asList(agent1, agent2);
        when(manager.getAgentDTOs()).thenReturn(dtos);
        Command.SHOW_AGENTS.execute(new String[] { "agents" }, new PrintWriter(out));

        // Trim variable padding spaces for testing
        String out = this.out.toString().replace(" ", "");
        assertTrue(out.contains("name1TYPE_EXTRUNNING"));
        assertTrue(out.contains("name2TIMERSET"));
    }

    @Test
    public void testShowAgentsDoesNotShowHiddenAgents()
    {
        AgentDTO agent1 = new AgentDTO("name1", "TYPE_EXT", "RUNNING", false);
        AgentDTO agent2 = new AgentDTO("name2", "TIMER", "SET", true);
        List<AgentDTO> dtos = Arrays.asList(agent1, agent2);
        when(manager.getAgentDTOs()).thenReturn(dtos);
        Command.SHOW_AGENTS.execute(new String[] { "agents" }, new PrintWriter(out));

        // Trim variable padding spaces for testing
        String out = this.out.toString().replace(" ", "");
        assertTrue(out.contains("name1TYPE_EXTRUNNING"));
        assertFalse("A hidden agent should not be displayed", out.contains("name2TIMERSET"));
    }

    @Test
    public void testShowAgentsWithOptionAIncludesHiddenAgents()
    {
        AgentDTO agent1 = new AgentDTO("name1", "TYPE_EXT", "RUNNING", false);
        AgentDTO agent2 = new AgentDTO("name2", "TIMER", "SET", true);
        List<AgentDTO> dtos = Arrays.asList(agent1, agent2);
        when(manager.getAgentDTOs()).thenReturn(dtos);
        Command.SHOW_AGENTS.execute(new String[] { "agents", "-a" }, new PrintWriter(out));

        // Trim variable padding spaces for testing
        String out = this.out.toString().replace(" ", "");
        assertTrue(out.contains("name1TYPE_EXTRUNNING"));
        assertTrue(out.contains("name2TIMERSET"));
    }

    @Test
    public void testShowAgentsWithOptionAllIncludesHiddenAgents()
    {
        AgentDTO agent1 = new AgentDTO("name1", "TYPE_EXT", "RUNNING", false);
        AgentDTO agent2 = new AgentDTO("name2", "TIMER", "SET", true);
        List<AgentDTO> dtos = Arrays.asList(agent1, agent2);
        when(manager.getAgentDTOs()).thenReturn(dtos);
        Command.SHOW_AGENTS.execute(new String[] { "agents", "--all" }, new PrintWriter(out));

        // Trim variable padding spaces for testing
        String out = this.out.toString().replace(" ", "");
        assertTrue(out.contains("name1TYPE_EXTRUNNING"));
        assertTrue(out.contains("name2TIMERSET"));
    }

    @Test
    public void testShowAgentsEmpty()
    {
        when(SystemUtils.getAllSystemTheadsDTOs()).thenReturn(Collections.emptyList());
        Command.SHOW_AGENTS.execute(new String[] { "agents" }, new PrintWriter(out));
        assertOutputEquals("No agent found");
    }

    @Test
    public void testHelp()
    {
        Command.HELP.execute(null, new PrintWriter(out));
        assertOutputContains("agents", "show-agents", "threads", "show-threads", "start", "stop", "run", "reset",
                "date", "uptime", "help");
    }

}
