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

    public final String name;
    public final String type;
    public final String state;
    public final boolean hidden;

    public AgentDTO(String name, String type, String state, boolean hidden)
    {
        this.name = name;
        this.type = type;
        this.state = state;
        this.hidden = hidden;
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
