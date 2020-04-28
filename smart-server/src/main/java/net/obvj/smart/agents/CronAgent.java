package net.obvj.smart.agents;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.util.DateUtils;

/**
 * An agent that runs a particular task at specified times and dates, similar to the Cron
 * service available in Unix/Linux systems.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public abstract class CronAgent extends Agent
{

    public static final String TYPE = "cron";

    private String cronExpression;
    private String cronDescription;

    private ScheduledExecutorService schedule;
    private Cron cron;

    private ZonedDateTime nextExecutionDate;

    /**
     * Builds a {@link CronAgent} from the given configuration.
     *
     * @param configuration the {@link AgentConfiguration} to be set
     */
    public CronAgent(AgentConfiguration configuration)
    {
        super(configuration);

        if (!TYPE.equalsIgnoreCase(configuration.getType()))
        {
            throw new IllegalArgumentException("Not a cron agent");
        }

        String originalExpression = configuration.getFrequency();
        cron = parseCron(originalExpression);
        cronExpression = cron.asString();
        cronDescription = CronDescriptor.instance().describe(cron);
        schedule = Executors.newSingleThreadScheduledExecutor();

        setState(State.SET);
    }

    protected static Cron parseCron(String expression)
    {
        CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX);
        CronParser cronParser = new CronParser(cronDefinition);
        return cronParser.parse(expression);
    }

    protected void scheduleNextExecution()
    {
        nextExecutionDate = null;
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        Optional<Duration> optional = executionTime.timeToNextExecution(DateUtils.now());

        if (optional.isPresent())
        {
            Duration timeToNextExecution = optional.get();
            schedule.schedule(this, timeToNextExecution.toMillis(), TimeUnit.MILLISECONDS);

            nextExecutionDate = DateUtils.now().plus(timeToNextExecution);
            LOG.log(Level.INFO, "Next execution for {0} will be at: {1}",
                    new Object[] { getName(), DateUtils.formatDate(nextExecutionDate) });
        }
        else
        {
            LOG.log(Level.WARNING, "No future execution for the Cron expression: \"{0}\"", cronExpression);
        }

    }

    /**
     * Starts this agent schedule considering the Cron expression.
     */
    @Override
    public final void onStart()
    {
        LOG.log(Level.INFO, "Agent {0} scheduled to run {1}.", new Object[] { getName(), cronDescription });
        scheduleNextExecution();
    }

    @Override
    public final void onStop()
    {
        schedule.shutdown();
        nextExecutionDate = null;
    }

    @Override
    public final void runTask()
    {
        doRunTask();
        scheduleNextExecution();
    }

    protected abstract void doRunTask();

    /**
     * @return A string with current agent status in JSON format
     */
    @Override
    public String getStatusString()
    {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
        builder.append("name", getName()).append("type", getType()).append("status", getState())
                .append("startDate", (DateUtils.formatDate(startDate)))
                .append("lastExecutionDate", (DateUtils.formatDate(lastRunDate)))
                .append("cronExpression", cronExpression).append("cronDescription", cronDescription)
                .append("nextExecutionDate", DateUtils.formatDate(nextExecutionDate));
        return builder.build();
    }

    /**
     * @return the Cron expression
     */
    public String getCronExpression()
    {
        return cronExpression;
    }

    /**
     * @return the next execution date
     */
    public ZonedDateTime getNextExecutionDate()
    {
        return nextExecutionDate;
    }

    protected ScheduledExecutorService getExecutorService()
    {
        return schedule;
    }
}
