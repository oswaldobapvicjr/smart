package net.obvj.smart.conf.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * An object that contains the set-up of an agent from the {@code agents.xml} file
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlAgent
{
    private static final String DEFAULT_INTERVAL = "1";
    private static final int DEFAULT_STOP_TIMEOUT_IN_SECONDS = -1;
    private static final boolean DEFAULT_AUTOMATICALLY_STARTED = true;

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

    public XmlAgent()
    {
    }

    private XmlAgent(Builder builder)
    {
        this.name = builder.name;
        this.type = builder.type;
        this.agentClass = builder.agentClass;
        this.interval = builder.interval;
        this.automaticallyStarted = builder.automaticallyStarted.booleanValue();
        this.stopTimeoutInSeconds = builder.stopTimeoutInSeconds.intValue();
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

    /**
     * A builder object for testing purposes.
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

        public XmlAgent build()
        {
            if (name == null) throw new IllegalStateException("name cannot be null");
            if (type == null) throw new IllegalStateException("type cannot be null");
            if (agentClass == null) throw new IllegalStateException("agentClass cannot be null");
            
            // The default interval can only be set for timer agents 
            if (interval == null && type.equals("timer")) interval = DEFAULT_INTERVAL;
            
            return new XmlAgent(this);
        }
    }
}
