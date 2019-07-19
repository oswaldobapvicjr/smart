package net.obvj.smart.agents.api.dto;

import java.io.Serializable;

public class AgentDTO implements Serializable
{
    private static final long serialVersionUID = 2513158698979426314L;

    public String name;
    public String type;
    public String state;

    public AgentDTO(String name, String type, String state)
    {
        super();
        this.name = name;
        this.type = type;
        this.state = state;
    }

}
