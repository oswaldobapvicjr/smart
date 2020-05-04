package net.obvj.smart.agents;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.obvj.smart.agents.impl.AnnotatedCronAgent;
import net.obvj.smart.agents.impl.AnnotatedTimerAgent;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.util.Exceptions;

/**
 * A factory that creates {@link Agent} objects based on given {@link AgentConfiguration}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentFactory
{

    private static final Map<String, Function<AgentConfiguration, Agent>> IMPLEMENTATIONS = new HashMap<>();

    static
    {
        IMPLEMENTATIONS.put("timer", AnnotatedTimerAgent::new);
        IMPLEMENTATIONS.put("cron", AnnotatedCronAgent::new);
    }

    private AgentFactory()
    {
        throw new IllegalStateException("No AgentFactory instances allowed");
    }

    /**
     * Creates a new Agent from the given {@link AgentConfiguration}.
     *
     * @throws NullPointerException     if a null agent configuration is received
     * @throws IllegalArgumentException if an unknown agent type is received
     * @since 2.0
     */
    public static Agent create(AgentConfiguration configuration)
    {
        String type = configuration.getType().toLowerCase();

        if (IMPLEMENTATIONS.containsKey(type))
        {
            return IMPLEMENTATIONS.get(type).apply(configuration);
        }
        throw Exceptions.illegalArgument("Unknown agent type: \"%s\"", type);
    }

}
