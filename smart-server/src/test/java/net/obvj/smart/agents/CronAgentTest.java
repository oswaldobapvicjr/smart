package net.obvj.smart.agents;

import static net.obvj.junit.utils.matchers.ExceptionMatcher.throwsException;
import static net.obvj.junit.utils.matchers.StringMatcher.containsAll;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.agents.Agent.State;
import net.obvj.smart.agents.impl.AnnotatedCronAgent;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.util.DateUtils;

/**
 * Unit tests for the {@link CronAgent} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtils.class)
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.*" })
public class CronAgentTest
{

    static
    {
        Locale.setDefault(Locale.UK);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    private static final ZonedDateTime DATE_20_04_27T23_25_13 = ZonedDateTime.of(2020, 4, 27, 23, 25, 13, 123456789, ZoneId.systemDefault());
    private static final ZonedDateTime DATE_20_04_27T23_26_00 = ZonedDateTime.of(2020, 4, 27, 23, 26,  0, 000000123, ZoneId.systemDefault());
    private static final ZonedDateTime DATE_20_04_27T23_30_00 = ZonedDateTime.of(2020, 4, 27, 23, 30,  0, 000000123, ZoneId.systemDefault());
    private static final ZonedDateTime DATE_20_04_28T02_00_00 = ZonedDateTime.of(2020, 4, 28, 2, 0, 0, 000000234,
            ZoneId.systemDefault());
    private static final ZonedDateTime DATE_20_05_04T00_00_00 = ZonedDateTime.of(2020, 5,  2,  0,  0,  0, 000000234, ZoneId.systemDefault());

    private static final String STR_CRON_EVERY_MINUTE = "* * * * *";
    private static final String STR_CRON_EVERY_30_MIN = "/30 * * * *";
    private static final String STR_CRON_EVERY_DAY_AT_2_AM = "0 2 * * *";
    private static final String STR_CRON_HOURLY_ON_WEEKEND = "0 * * * SAT,SUN";

    private static final String AGENT_NAME = "DummyAgent";
    private static final String AGENT_CLASS = "net.obvj.smart.agents.test.valid.TestAgentWithNoNameAndTypeCronAndAgentTask";

    private static final AgentConfiguration AGENT_CFG_EVERY_MINUTE = new AgentConfiguration.Builder("cron")
            .name(AGENT_NAME).agentClass(AGENT_CLASS).frequency(STR_CRON_EVERY_MINUTE).build();

    private static final AgentConfiguration AGENT_CFG_EVERY_30_MIN = new AgentConfiguration.Builder("cron")
            .name(AGENT_NAME).agentClass(AGENT_CLASS).frequency(STR_CRON_EVERY_30_MIN).build();

    private static final AgentConfiguration AGENT_CFG_EVERY_DAY_AT_2_AM = new AgentConfiguration.Builder("cron")
            .name(AGENT_NAME).agentClass(AGENT_CLASS).frequency(STR_CRON_EVERY_DAY_AT_2_AM).build();

    private static final AgentConfiguration AGENT_CFG_HOURLY_ON_WEEKEND = new AgentConfiguration.Builder("cron")
            .name(AGENT_NAME).agentClass(AGENT_CLASS).frequency(STR_CRON_HOURLY_ON_WEEKEND).build();

    private static final CronAgent AGENT_EVERY_MINUTE = spy((CronAgent) AgentFactory.create(AGENT_CFG_EVERY_MINUTE));
    private static final CronAgent AGENT_EVERY_30_MIN = spy((CronAgent) AgentFactory.create(AGENT_CFG_EVERY_30_MIN));
    private static final CronAgent AGENT_EVERY_DAY_AT_2_AM = spy((CronAgent) AgentFactory.create(AGENT_CFG_EVERY_DAY_AT_2_AM));
    private static final CronAgent AGENT_HOURLY_ON_WEEKEND = spy((CronAgent) AgentFactory.create(AGENT_CFG_HOURLY_ON_WEEKEND));

    private static final AgentConfiguration DUMMY_AGENT_CONFIG = new AgentConfiguration.Builder("cron")
            .name(AGENT_NAME).agentClass(AGENT_CLASS).frequency("0 0 * * 0").automaticallyStarted(false)
            .stopTimeoutInSeconds(5).build();


    private CronAgent agentMock = mock(CronAgent.class, Mockito.CALLS_REAL_METHODS);

    /**
     * Tests that a non-cron agent will not be parsed by this class
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseNonCronAgent() throws ReflectiveOperationException
    {
        new AnnotatedCronAgent(new AgentConfiguration.Builder("timer").name(AGENT_NAME)
                .agentClass("net.obvj.smart.agents.dummy.DummyAgent").build());
    }

    @Test
    public void testScheduleNextExecutions()
    {
        PowerMockito.mockStatic(DateUtils.class);
        PowerMockito.when(DateUtils.now()).thenReturn(DATE_20_04_27T23_25_13);

        AGENT_EVERY_MINUTE.scheduleNextExecution();
        AGENT_EVERY_30_MIN.scheduleNextExecution();
        AGENT_EVERY_DAY_AT_2_AM.scheduleNextExecution();
        AGENT_HOURLY_ON_WEEKEND.scheduleNextExecution();

        assertEqualDatesIgnoringNanos(DATE_20_04_27T23_26_00, AGENT_EVERY_MINUTE.getNextExecutionDate());
        assertEqualDatesIgnoringNanos(DATE_20_04_27T23_30_00, AGENT_EVERY_30_MIN.getNextExecutionDate());
        assertEqualDatesIgnoringNanos(DATE_20_04_28T02_00_00, AGENT_EVERY_DAY_AT_2_AM.getNextExecutionDate());
        assertEqualDatesIgnoringNanos(DATE_20_05_04T00_00_00, AGENT_HOURLY_ON_WEEKEND.getNextExecutionDate());
    }

    @Test
    public void testScheduleNextExecutionOnStart()
    {
        PowerMockito.mockStatic(DateUtils.class);
        PowerMockito.when(DateUtils.now()).thenReturn(DATE_20_04_27T23_25_13);

        AGENT_EVERY_DAY_AT_2_AM.start();
        assertEqualDatesIgnoringNanos(DATE_20_04_28T02_00_00, AGENT_EVERY_DAY_AT_2_AM.getNextExecutionDate());
    }

    @Test
    public void testScheduleNextExecutionAfterRun()
    {
        PowerMockito.mockStatic(DateUtils.class);
        PowerMockito.when(DateUtils.now()).thenReturn(DATE_20_04_27T23_25_13);

        AGENT_EVERY_DAY_AT_2_AM.run();
        assertEqualDatesIgnoringNanos(DATE_20_04_28T02_00_00, AGENT_EVERY_DAY_AT_2_AM.getNextExecutionDate());
    }

    private void assertEqualDatesIgnoringNanos(ZonedDateTime expectedDate, ZonedDateTime actualDate)
    {
        ZonedDateTime tmpExpectedDate = expectedDate.withNano(0);
        ZonedDateTime tmpActualDate = actualDate.withNano(0);
        assertThat(tmpActualDate, is(equalTo(tmpExpectedDate)));
    }

    @Test
    public void testScheduleOnStop() throws TimeoutException
    {
        CronAgent agent = PowerMockito.spy((CronAgent) AgentFactory.create(AGENT_CFG_EVERY_MINUTE));
        agent.stop();
        assertThat(agent.getExecutorService().isShutdown(), is(true));
        assertThat(agent.getNextExecutionDate(), is(nullValue()));
    }

    /**
     * Tests that no action is taken when start() is called on a started agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStarted()
    {
        when(agentMock.getState()).thenReturn(State.STARTED);
        assertThat(() -> agentMock.start(), throwsException(IllegalStateException.class)
                .withMessageContaining(CronAgent.MSG_AGENT_ALREADY_STARTED));
    }

    /**
     * Tests that no action is taken when start() is called on a stopped agent.
     */
    @Test
    public void testStartAgentWithPreviousStateStopped()
    {
        when(agentMock.getState()).thenReturn(State.STOPPED);
        assertThat(() -> agentMock.start(), throwsException(IllegalStateException.class));
    }

    /**
     * Tests that no action is taken when stop() is called on a stopped agent.
     */
    @Test
    public void testStopAgentWithPreviousStateStopped()
    {
        when(agentMock.isStopped()).thenReturn(true);
        assertThat(() ->
        {
            try
            {
                agentMock.stop();
            }
            catch (TimeoutException e)
            {
                fail("timeout");
            }
        }, throwsException(IllegalStateException.class).withMessageContaining(CronAgent.MSG_AGENT_ALREADY_STOPPED));
    }

    /**
     * Tests that no action is taken when run() is called on a running agent.
     */
    @Test
    public void testRunAgentWithPreviousStateRunning()
    {
        when(agentMock.isRunning()).thenReturn(true);
        agentMock.run();
        verify(agentMock, never()).runTask();
    }

    /**
     * Tests that an exception is thrown when run(true) is called on a running agent.
     */
    @Test(expected = IllegalStateException.class)
    public void testRunManuallyAgentWithPreviousStateRunning()
    {
        when(agentMock.isRunning()).thenReturn(true);
        agentMock.run(true);
    }

    @Test
    public void testRunAgentWithPreviousStateSet() throws ReflectiveOperationException
    {
        CronAgent agent = spy((CronAgent) AgentFactory.create(DUMMY_AGENT_CONFIG));
        assertThat("State before run() should be SET", agent.getState(), is(State.SET));
        agent.run();
        verify(agent).runTask();
        assertThat("State after start() should be SET", agent.getState(), is(State.SET));
        assertThat(agent.getLastRunDate(), is(notNullValue()));
    }

    @Test
    public void testGetAgentStatusStr() throws ReflectiveOperationException
    {
        CronAgent agent = (CronAgent) AgentFactory.create(DUMMY_AGENT_CONFIG);
        String statusWithoutQuotes = agent.getStatusString().replace("\"", "");
        assertThat(statusWithoutQuotes, containsAll("name:DummyAgent", "type:cron", "status:SET",
                "startDate:null", "lastExecutionDate:null", "cronExpression:0 0 * * 0", "cronDescription",
                "nextExecutionDate"));
    }

    @Test
    public void testGetCronExpression()
    {
        CronAgent agent = (CronAgent) AgentFactory.create(DUMMY_AGENT_CONFIG);
        assertThat(agent.getCronExpression(), is(equalTo("0 0 * * 0")));
    }

}
