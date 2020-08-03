package net.obvj.smart.agents;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.Agent.State;

/**
 * Unit tests for the {@link Agent} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.*" })
public class AgentTest
{
    // The setting CALLS_REAL_METHODS allows mocking abstract methods/classes
    Agent agent = PowerMockito.mock(Agent.class, Mockito.CALLS_REAL_METHODS);

    @Test
    public void testStatusMethodsForAgentStatusSet()
    {
        agent.setState(State.SET);
        assertFalse("expected false on agent.isStarted()", agent.isStarted());
        assertFalse("expected false on agent.isRunning()", agent.isRunning());
        assertFalse("expected false on agent.isStopped()", agent.isStopped());
    }

    @Test
    public void testStatusMethodsForAgentStatusStarted()
    {
        agent.setState(State.STARTED);
        assertTrue("expected true on agent.isStarted()", agent.isStarted());
        assertFalse("expected false on agent.isRunning()", agent.isRunning());
        assertFalse("expected false on agent.isStopped()", agent.isStopped());
    }

    @Test
    public void testStatusMethodsForAgentStatusRunning()
    {
        agent.setState(State.RUNNING);
        assertFalse("expected false on agent.isStarted()", agent.isStarted());
        assertTrue("expected true on agent.isRunning()", agent.isRunning());
        assertFalse("expected false on agent.isStopped()", agent.isStopped());
    }

    @Test
    public void testStatusMethodsForAgentStatusStopped()
    {
        agent.setState(State.STOPPED);
        assertFalse("expected false on agent.isStarted()", agent.isStarted());
        assertFalse("expected false on agent.isRunning()", agent.isRunning());
        assertTrue("expected true on agent.isStopped()", agent.isStopped());
    }

    @Test
    public void testStartDate()
    {
        Calendar now = Calendar.getInstance();
        agent.startDate = now;
        Calendar startDate = agent.getStartDate();
        assertNotSame(now, startDate);
        assertThat(startDate.getTime(), is(now.getTime()));
    }

    @Test
    public void testStartDateNull()
    {
        agent.startDate = null;
        Calendar lastRunDate = agent.getStartDate();
        assertNull(lastRunDate);
    }

    @Test
    public void testLastRunDate()
    {
        Calendar now = Calendar.getInstance();
        agent.lastExecutionDate = now;
        Calendar lastRunDate = agent.getLastRunDate();
        assertNotSame(now, lastRunDate);
        assertThat(lastRunDate.getTime(), is(now.getTime()));
    }

    @Test
    public void testLastRunDateNull()
    {
        agent.lastExecutionDate = null;
        Calendar lastRunDate = agent.getLastRunDate();
        assertNull(lastRunDate);
    }

}
