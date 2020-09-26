package net.obvj.smart.agents;

import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.util.ReflectionUtils;

import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.conf.AgentConfigurationException;
import net.obvj.smart.conf.annotation.AgentTask;
import net.obvj.smart.util.AnnotationUtils;

/**
 * An object that prepares and holds the required metadata and infrastructure for the
 * execution of an object annotated as {@code @Agent}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AnnotatedAgent
{
    private final Class<?> agentClass;
    private final Method agentTaskMethod;
    private final Object agentInstance;

    /**
     * Validates annotations and prepares all objects for execution.
     *
     * @param configuration the {@link AgentConfiguration} to be parsed
     * @throws AgentConfigurationException if any exception regarding a reflective operation
     *                                     (e.g.: class or method not found) occurs
     */
    public AnnotatedAgent(AgentConfiguration configuration)
    {
        try
        {
            String agentClassName = configuration.getAgentClass();
            agentClass = Class.forName(agentClassName);
            agentTaskMethod = AnnotationUtils.getSingleMethodWithAnnotation(agentClass, AgentTask.class);
            agentInstance = ConstructorUtils.invokeConstructor(agentClass);
        }
        catch (ReflectiveOperationException cause)
        {
            throw new AgentConfigurationException(cause);
        }
    }

    /**
     * Invokes the method annotated as {@code @AgentTask} for the agent.
     */
    public void runAgentTask()
    {
        ReflectionUtils.invokeMethod(agentTaskMethod, agentInstance);
    }

    /**
     * @return the agentClass
     */
    public Class<?> getAgentClass()
    {
        return agentClass;
    }

    /**
     * @return the annotated agent's task method
     */
    public Method getAgentTaskMethod()
    {
        return agentTaskMethod;
    }

    /**
     * @return the annotated agent's instance
     */
    public Object getAgentInstance()
    {
        return agentInstance;
    }

}
