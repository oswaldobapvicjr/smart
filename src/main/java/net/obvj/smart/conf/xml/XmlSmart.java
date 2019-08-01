package net.obvj.smart.conf.xml;

import java.util.List;

import javax.xml.bind.annotation.*;

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

    public void setAgents(List<XmlAgent> agents)
    {
        this.agents = agents;
    }

}
