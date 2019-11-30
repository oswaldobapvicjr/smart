package net.obvj.smart.agents.api.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * An object for common agent metadata interchange with client applications
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentDTO implements Serializable
{
    private static final long serialVersionUID = 2513158698979426314L;

    private final String name;
    private final String type;
    private final String state;
    private final boolean hidden;

    public AgentDTO(String name, String type, String state, boolean hidden)
    {
        this.name = name;
        this.type = type;
        this.state = state;
        this.hidden = hidden;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public String getState()
    {
        return state;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, state, type);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof AgentDTO)) return false;
        AgentDTO other = (AgentDTO) obj;
        return Objects.equals(name, other.name) && Objects.equals(state, other.state)
                && Objects.equals(type, other.type) && Objects.equals(hidden, other.hidden);
    }

}
