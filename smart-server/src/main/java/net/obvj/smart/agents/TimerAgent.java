package net.obvj.smart.agents;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.util.DateUtils;
import net.obvj.smart.util.TimeInterval;
import net.obvj.smart.util.TimeUnit;

/**
 * A thread-safe extensible Agent for tasks that are scheduled in the system to run
 * repeatedly, given an interval that is particular to each task. Available operations
 * are: 'start', 'stop', 'run' and 'reset'
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public abstract class TimerAgent extends Agent
{
    public static final String TYPE = "timer";

    private TimeInterval interval;

    private AgentThreadFactory threadFactory;
    private ScheduledExecutorService schedule;

    /**
     * Builds a {@link TimerAgent} from the given configuration.
     *
     * @param configuration the {@link AgentConfiguration} to be set
     */
    public TimerAgent(AgentConfiguration configuration)
    {
        super(configuration);

        if (!TYPE.equalsIgnoreCase(configuration.getType()))
        {
            throw new IllegalArgumentException("Not a timer agent");
        }

        TimeInterval timeInterval = TimeInterval.of(configuration.getFrequency());
        this.interval = timeInterval;

        threadFactory = new AgentThreadFactory(getName());
        schedule = Executors.newSingleThreadScheduledExecutor(threadFactory);

        setState(State.SET);
    }

    /**
     * Starts this agent timer considering the interval settled in this object for execution.
     */
    @Override
    public final void onStart()
    {
        Date start = DateUtils.getExactStartDateEvery(interval.getDuration(), interval.getTimeUnit());
        schedule.scheduleAtFixedRate(this, (start.getTime() - System.currentTimeMillis()), interval.toMillis(),
                java.util.concurrent.TimeUnit.MILLISECONDS);

        if (LOG.isInfoEnabled())
        {
            LOG.info("Agent {} scheduled to run every {}. First execution will be at: {}", getName(), interval,
                    DateUtils.formatDate(start));
        }
    }

    /**
     * Terminates this agent timer gracefully. Does not interfere with a currently executing
     * task, if it exists.
     */
    @Override
    public final void onStop()
    {
        schedule.shutdown();
    }

    /**
     * @return A string with current agent status in JSON format
     */
    @Override
    public String getStatusString()
    {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
        builder.append("name", getName()).append("type", getType()).append("status", getState())
                .append("startDate", (DateUtils.formatDate(startDate)))
                .append("lastRunDate", (DateUtils.formatDate(lastRunDate))).append("frequency", interval);
        return builder.build();
    }

    public TimeInterval getFrequency()
    {
        return new TimeInterval(interval);
    }

    public int getIntervalDuration()
    {
        return interval.getDuration();
    }

    public TimeUnit getTimeUnit()
    {
        return interval.getTimeUnit();
    }

}
