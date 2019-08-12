package net.obvj.smart.conf.xml;

import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * The root element of the {@code agents.xml} file
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@XmlRootElement(name = "smart")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlSmart
{
    @XmlElementWrapper(name = "agents")
    @XmlElement(name = "agent")
    private List<XmlAgent> agents;

    public List<XmlAgent> getAgents()
    {
        return agents;
    }

}
