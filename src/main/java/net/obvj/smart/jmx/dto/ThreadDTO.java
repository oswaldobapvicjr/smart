package net.obvj.smart.jmx.dto;

import java.io.Serializable;

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

}
