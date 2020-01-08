package net.obvj.smart.agents.impl;

import java.lang.reflect.Method;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.util.ReflectionUtils;

import net.obvj.smart.agents.TimerAgent;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.conf.AgentConfigurationException;
import net.obvj.smart.conf.annotation.AgentTask;
import net.obvj.smart.util.AnnotationUtils;

/**
 * A {@link TimerAgent} that runs an object annotated with {@code @Agent}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AnnotatedTimerAgent extends TimerAgent
{
    private Method annotatedAgentTaskMethod;
    private Object annotatedAgentInstance;

    public AnnotatedTimerAgent(AgentConfiguration configuration)
    {
        super(configuration);
        init();
    }

    /**
     * Validates annotations and prepares all objects for execution.
     */
    private void init()
    {
        try
        {
            String annotatedAgentClassName = super.getConfiguration().getAgentClass();
            Class<?> annotatedAgentClass = Class.forName(annotatedAgentClassName);
            annotatedAgentTaskMethod = AnnotationUtils.getSingleMethodWithAnnotation(annotatedAgentClass,
                    AgentTask.class);
            annotatedAgentInstance = ConstructorUtils.invokeConstructor(annotatedAgentClass);
        }
        catch (ReflectiveOperationException e)
        {
            throw new AgentConfigurationException(e);
        }
    }

    /**
     * Executes the method annotated with {@code AgentTask} in the annotated agent instance.
     */
    @Override
    protected void runTask()
    {
        ReflectionUtils.invokeMethod(annotatedAgentTaskMethod, annotatedAgentInstance);
    }

    /**
     * @return the annotatedAgentTaskMethod
     */
    protected Method getAnnotatedAgentTaskMethod()
    {
        return annotatedAgentTaskMethod;
    }

    /**
     * @return the annotatedAgentInstance
     */
    protected Object getAnnotatedAgentInstance()
    {
        return annotatedAgentInstance;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("AnnotatedTimerAgent$").append(annotatedAgentInstance.getClass().getName());
        return builder.toString();
    }

}
