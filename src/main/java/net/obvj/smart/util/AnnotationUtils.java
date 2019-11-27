package net.obvj.smart.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.reflect.MethodUtils;

import net.obvj.smart.conf.AgentConfigurationException;

public class AnnotationUtils
{
    private AnnotationUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Returns the method of the given class that is annotated with the given annotation,
     * provided that only a single method containing this annotation exists in the class.
     * 
     * @param class           the {@link Class} to query
     * @param annotationClass the annotation that must be present on a method to be matched
     * @return a {@link Method}
     * @throws AgentConfigurationException if either no method or more than one method found
     */
    public static Method getSingleMethodWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass)
    {
        List<Method> agentTaskMethods = MethodUtils.getMethodsListWithAnnotation(clazz, annotationClass);

        if (agentTaskMethods.isEmpty())
        {
            throw Exceptions.agentConfiguration("No public method annotated with @AgentTask found in %s", clazz.getName());
        }
        if (agentTaskMethods.size() > 1)
        {
            throw Exceptions.agentConfiguration(
                    "%s methods annotated with @AgentTask found in %s. Only one is allowed.", agentTaskMethods.size(),
                    clazz.getName());
        }
        return agentTaskMethods.get(0);
    }
}
