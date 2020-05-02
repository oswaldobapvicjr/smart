package net.obvj.smart.jmx.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * An object for common thread metadata interchange with client applications.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ThreadDTO implements Serializable
{
    private static final long serialVersionUID = -6159494396012110476L;

    private final long id;
    private final String name;
    private final String state;

    /**
     * Builds this ThreadDTO with all fields set.
     *
     * @param id    the thread id to set
     * @param name  the thread name to set
     * @param state the thread state to set
     */
    public ThreadDTO(long id, String name, String state)
    {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    /**
     * @return the thread ID
     */
    public long getId()
    {
        return id;
    }

    /**
     * @return the thread name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the thread state
     */
    public String getState()
    {
        return state;
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
        return Objects.hash(id, name, state);
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof ThreadDTO)) return false;
        ThreadDTO other = (ThreadDTO) obj;
        return id == other.id && Objects.equals(name, other.name) && Objects.equals(state, other.state);
    }

}
