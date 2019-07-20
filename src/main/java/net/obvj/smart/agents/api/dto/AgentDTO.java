package net.obvj.smart.agents.api.dto;

import java.io.Serializable;

/**
 * An object for common agent metadata interchange with client applications
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentDTO implements Serializable
{
    private static final long serialVersionUID = 2513158698979426314L;

    public final String name;
    public final String type;
    public final String state;

    public AgentDTO(String name, String type, String state)
    {
        this.name = name;
        this.type = type;
        this.state = state;
    }

}
