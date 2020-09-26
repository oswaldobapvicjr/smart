package net.obvj.smart.agents.impl;

import net.obvj.smart.agents.AnnotatedAgent;
import net.obvj.smart.agents.TimerAgent;
import net.obvj.smart.conf.AgentConfiguration;

/**
 * A {@link TimerAgent} that runs an object annotated with {@code @Agent}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AnnotatedTimerAgent extends TimerAgent
{
    private final AnnotatedAgent annotatedAgent;

    public AnnotatedTimerAgent(AgentConfiguration configuration)
    {
        super(configuration);
        annotatedAgent = new AnnotatedAgent(configuration);
    }

    /**
     * Executes the method annotated with {@code AgentTask} in the annotated agent instance.
     */
    @Override
    protected void runTask()
    {
        annotatedAgent.runAgentTask();
    }

    /**
     * @return the metadata
     */
    protected AnnotatedAgent getMetadata()
    {
        return annotatedAgent;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AnnotatedTimerAgent$").append(annotatedAgent.getAgentClass().getName());
        return builder.toString();
    }

}
