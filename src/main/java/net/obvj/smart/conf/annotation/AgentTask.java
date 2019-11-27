package net.obvj.smart.conf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identify the annotated method as the one that implements the task to be executed for a
 * class with annotated with {@code @Agent}.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface AgentTask
{
    // Marker annotation
}
