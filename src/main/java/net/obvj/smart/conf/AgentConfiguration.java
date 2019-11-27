package net.obvj.smart.conf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.StringUtils;

import net.obvj.smart.agents.api.DaemonAgent;
import net.obvj.smart.agents.api.TimerAgent;
import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.Type;
import net.obvj.smart.util.Exceptions;

/**
 * An object that contains the set-up of an agent from the {@code agents.xml} file
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentConfiguration
{
    protected static final String DEFAULT_INTERVAL = "1";
    protected static final int DEFAULT_STOP_TIMEOUT_IN_SECONDS = -1;
    protected static final boolean DEFAULT_AUTOMATICALLY_STARTED = true;
    protected static final boolean DEFAULT_HIDDEN = false;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "type")
    private String type;

    @XmlElement(name = "class")
    private String agentClass;

    @XmlElement(name = "interval")
    private String interval = DEFAULT_INTERVAL;

    @XmlElement(name = "started")
    private boolean automaticallyStarted = DEFAULT_AUTOMATICALLY_STARTED;

    @XmlElement(name = "stopTimeoutInSeconds")
    private int stopTimeoutInSeconds = DEFAULT_STOP_TIMEOUT_IN_SECONDS;

    @XmlElement(name = "hidden")
    private boolean hidden = DEFAULT_HIDDEN;

    public AgentConfiguration()
    {
    }

    private AgentConfiguration(Builder builder)
    {
        this.name = builder.name;
        this.type = builder.type;
        this.agentClass = builder.agentClass;
        this.interval = builder.interval;
        this.automaticallyStarted = builder.automaticallyStarted.booleanValue();
        this.stopTimeoutInSeconds = builder.stopTimeoutInSeconds.intValue();
        this.hidden = builder.hidden;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public String getAgentClass()
    {
        return agentClass;
    }

    public String getInterval()
    {
        return interval;
    }

    public boolean isAutomaticallyStarted()
    {
        return automaticallyStarted;
    }

    public int getStopTimeoutInSeconds()
    {
        return stopTimeoutInSeconds;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    /**
     * An {@link AgentConfiguration} builder.
     * 
     * @author oswaldo.bapvic.jr
     * @since 2.0
     */
    public static class Builder
    {
        private String name;
        private String type;
        private String agentClass;
        private String interval;
        private Boolean automaticallyStarted = Boolean.valueOf(DEFAULT_AUTOMATICALLY_STARTED);
        private Integer stopTimeoutInSeconds = Integer.valueOf(DEFAULT_STOP_TIMEOUT_IN_SECONDS);
        private Boolean hidden = Boolean.valueOf(DEFAULT_HIDDEN);

        public Builder(String name)
        {
            this.name = name;
        }

        public Builder type(String type)
        {
            this.type = type;
            return this;
        }

        public Builder agentClass(String agentClass)
        {
            this.agentClass = agentClass;
            return this;
        }

        public Builder interval(String interval)
        {
            this.interval = interval;
            return this;
        }

        public Builder automaticallyStarted(boolean automaticallyStarted)
        {
            this.automaticallyStarted = Boolean.valueOf(automaticallyStarted);
            return this;
        }

        public Builder stopTimeoutInSeconds(int stopTimeoutInSeconds)
        {
            this.stopTimeoutInSeconds = Integer.valueOf(stopTimeoutInSeconds);
            return this;
        }

        public Builder hidden(boolean hidden)
        {
            this.hidden = Boolean.valueOf(hidden);
            return this;
        }

        public AgentConfiguration build()
        {
            if (StringUtils.isEmpty(name)) throw new IllegalStateException("name cannot be null");
            if (StringUtils.isEmpty(type)) throw new AgentConfigurationException("type cannot be null");
            if (agentClass == null) throw new AgentConfigurationException("agentClass cannot be null");

            // The default interval can only be set for timer agents
            if (StringUtils.isEmpty(interval) && type.equals("timer")) interval = DEFAULT_INTERVAL;

            return new AgentConfiguration(this);
        }
    }

    public static AgentConfiguration fromAnnotatedClass(Class<?> clazz)
    {
        Agent annotation = clazz.getAnnotation(Agent.class);
        if (annotation == null)
        {
            throw Exceptions.agentConfiguration("@Agent annotation is not present in class %s", clazz);
        }

        // Name: If not specified in annotation, then use the class simple name
        String name = StringUtils.defaultIfEmpty(annotation.name(), clazz.getSimpleName());

        // Type: If not specified in annotation, try to infer by the class it implements
        String type = Type.DEFAULT == annotation.type() ? inferTypeFromSuperclass(clazz) : annotation.type().toString();

        String agentClass = clazz.getCanonicalName();
        String interval = annotation.interval();
        int stopTimeoutInSeconds = annotation.stopTimeoutInSeconds();
        boolean automaticallyStarted = annotation.automaticallyStarted();
        boolean hidden = annotation.hidden();

        Builder builder = new Builder(name).type(type).agentClass(agentClass).interval(interval)
                .stopTimeoutInSeconds(stopTimeoutInSeconds).automaticallyStarted(automaticallyStarted).hidden(hidden);
        return builder.build();
    }

    private static String inferTypeFromSuperclass(Class<?> clazz)
    {
        Class<?> superClass = clazz.getSuperclass();
        if (superClass.equals(TimerAgent.class))
        {
            return TimerAgent.TYPE;
        }
        else if (superClass.equals(DaemonAgent.class))
        {
            return DaemonAgent.TYPE;
        }
        return StringUtils.EMPTY;
    }

}
