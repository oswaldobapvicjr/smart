package net.obvj.smart.conf.xml;

import java.util.List;

import javax.xml.bind.annotation.*;

import net.obvj.smart.conf.AgentConfiguration;

/**
 * The root element of the {@code agents.xml} file
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@XmlRootElement(name = "smart")
@XmlAccessorType(XmlAccessType.FIELD)
public class SmartConfiguration
{
    @XmlElementWrapper(name = "agents")
    @XmlElement(name = "agent")
    private List<AgentConfiguration> agents;

    public List<AgentConfiguration> getAgents()
    {
        return agents;
    }

}
