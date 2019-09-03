package net.obvj.smart.jmx.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * An object for common thread metadata interchange with client applications
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ThreadDTO implements Serializable
{
    private static final long serialVersionUID = -6159494396012110476L;

    public final long id;
    public final String name;
    public final String state;

    public ThreadDTO(long id, String name, String state)
    {
        this.id = id;
        this.name = name;
        this.state = state;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, name, state);
    }

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
