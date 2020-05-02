package net.obvj.smart.agents.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * An object for common agent metadata interchange with client applications.
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

    /**
     * Builds this Agent with all fields set.
     *
     * @param name   the agent name to set
     * @param type   the agent type to set
     * @param state  the agent state to set
     * @param hidden the hidden flag to set
     */
    public AgentDTO(String name, String type, String state, boolean hidden)
    {
        this.name = name;
        this.type = type;
        this.state = state;
        this.hidden = hidden;
    }

    /**
     * @return the agent name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the agent type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @return the agent state
     */
    public String getState()
    {
        return state;
    }

    /**
     * @return a flag indicating whether this is a hidden agent or not
     */
    public boolean isHidden()
    {
        return hidden;
    }

    /**
     * Returns a hash code value for the object, to support hash tables.
     *
     * @return the hash code
     * @see Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(name, state, type);
    }

    /**
     * @see Object#equals(Object)
     */
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
