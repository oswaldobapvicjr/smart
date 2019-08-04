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
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "type")
    private String type;

    @XmlElement(name = "class")
    private String agentClass;

    @XmlElement(name = "intervalInMinutes")
    private int intervalInMinutes;

    @XmlElement(name = "started")
    private boolean automaticallyStarted;

    @XmlElement(name = "stopTimeoutInSeconds")
    private int stopTimeoutInSeconds;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getAgentClass()
    {
        return agentClass;
    }

    public void setAgentClass(String agentClass)
    {
        this.agentClass = agentClass;
    }

    public int getIntervalInMinutes()
    {
        return intervalInMinutes;
    }

    public void setIntervalInMinutes(int intervalInMinutes)
    {
        this.intervalInMinutes = intervalInMinutes;
    }

    public boolean isAutomaticallyStarted()
    {
        return automaticallyStarted;
    }

    public void setAutomaticallyStarted(boolean automaticallyStarted)
    {
        this.automaticallyStarted = automaticallyStarted;
    }

    public int getStopTimeoutInSeconds()
    {
        return stopTimeoutInSeconds;
    }

    public void setStopTimeoutInSeconds(int stopTimeoutInSeconds)
    {
        this.stopTimeoutInSeconds = stopTimeoutInSeconds;
    }

}
