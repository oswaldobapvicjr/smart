package net.obvj.smart.conf.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identify the annotated class as an Agent.
 * <p>
 * The annotated class is the implementation class of the Agent.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Agent
{
    /**
     * The name of this Agent.
     * <p>
     * If not specified, the name of this Agent will be the simple name of the class being
     * annotated.
     */
    String name() default "";

    /**
     * The type of this Agent.
     * <p>
     * If not specified, the type of this Agent will be inferred from the super class.
     */
    Type type() default Type.DEFAULT;

    /**
     * The interval between executions for this Agent (only applicable for Timer Agents).
     * <p>
     * If not specified, the default interval of 1 minute will be considered.
     */
    String interval() default "1";

    /**
     * Declares whether this Agent is started when the server is started.
     * <p>
     * If {@code true} or not specified, this Agent is automatically started.
     */
    boolean automaticallyStarted() default true;

    /**
     * Declares the timeout in seconds to wait for the Agent task to be finished when a stop
     * request is received.
     * <p>
     * If not specified, no timeout on stop.
     */
    int stopTimeoutInSeconds() default Integer.MAX_VALUE;

    /**
     * Declares whether this Agent is hidden in the console (default = false).
     */
    boolean hidden() default false;

}
