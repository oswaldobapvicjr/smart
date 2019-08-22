package net.obvj.smart.jmx;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.jmx.dto.ThreadDTO;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.DateUtil;
import net.obvj.smart.util.SystemUtil;

/**
 * Unit tests for the {@link AgentManagerJMX} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({AgentManager.class, DateUtil.class, SystemUtil.class})
public class AgentManagerJMXTest
{
    private static final String AGENT1 = "Agent1";

    @Mock
    private AgentManager manager;

    // Test subject
    private AgentManagerJMX jmx = new AgentManagerJMX();

    @Before
    public void setup()
    {
        mockStatic(AgentManager.class);
        when(AgentManager.getInstance()).thenReturn(manager);
        
        mockStatic(DateUtil.class);
        mockStatic(SystemUtil.class);
    }

    @Test
    public void testStartAgent()
    {
        jmx.startAgent(AGENT1);
        verify(manager).startAgent(AGENT1);
    }

    @Test
    public void testRunAgent()
    {
        jmx.runNow(AGENT1);
        verify(manager).runNow(AGENT1);
    }

    @Test
    public void testStopAgent() throws TimeoutException
    {
        jmx.stopAgent(AGENT1);
        verify(manager).stopAgent(AGENT1);
    }

    @Test
    public void testResetAgent()
    {
        jmx.resetAgent(AGENT1);
        verify(manager).resetAgent(AGENT1);
    }

    @Test
    public void testIsAgentStarted()
    {
        when(manager.isAgentStarted(AGENT1)).thenReturn(true);
        assertTrue(jmx.isAgentStarted(AGENT1));
        verify(manager).isAgentStarted(AGENT1);
    }

    @Test
    public void testIsAgentRunning()
    {
        when(manager.isAgentRunning(AGENT1)).thenReturn(true);
        assertTrue(jmx.isAgentRunning(AGENT1));
        verify(manager).isAgentRunning(AGENT1);
    }

    @Test
    public void testAgentStatusStr()
    {
        when(manager.getAgentStatusStr(AGENT1)).thenReturn("statusAgent1");
        assertEquals("statusAgent1", jmx.getAgentStatusStr(AGENT1));
    }

    @Test
    public void testGetAgentNames()
    {
        String[] names = new String[] { "name1", "name2" };
        when(manager.getAgentNames()).thenReturn(names);
        assertArrayEquals(names, jmx.getAgentNames());
    }

    @Test
    public void testGetAgentDTOs()
    {
        AgentDTO agent1 = new AgentDTO("name1", "TIMER", "STARTED");
        AgentDTO agent2 = new AgentDTO("name2", "DAEMON", "STOPPED");
        List<AgentDTO> dtos = Arrays.asList(agent1, agent2);
        when(manager.getAgentDTOs()).thenReturn(dtos);
        assertTrue(jmx.getAgentDTOs().containsAll(dtos));
    }
    
    @Test
    public void testGetServerDate()
    {
        when(DateUtil.now()).thenReturn("date1");
        assertEquals("date1", jmx.getServerDate());
    }
    
    @Test
    public void testGetServerUptime()
    {
        when(SystemUtil.getSystemUptime()).thenReturn(987l);
        assertEquals(987l, jmx.getServerUptime());
    }
    
    @Test
    public void testGetAllThreadsInfo()
    {
        ThreadDTO thread1 = new ThreadDTO(1, "name1", "RUNNABLE");
        ThreadDTO thread2 = new ThreadDTO(2, "name2", "WAITING");
        List<ThreadDTO> dtos = Arrays.asList(thread1, thread2);
        when(SystemUtil.getAllSystemTheadsDTOs()).thenReturn(dtos);
        assertTrue(jmx.getAllThreadsInfo().containsAll(dtos));
    }
  
}
