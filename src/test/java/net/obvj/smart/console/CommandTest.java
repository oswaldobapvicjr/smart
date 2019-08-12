package net.obvj.smart.console;

import static org.junit.Assert.*;

import org.junit.Test;

public class CommandTest
{
    @Test
    public void testCommandsByNameOrAliasWithValidNames()
    {
        assertEquals(Command.DATE, Command.getByNameOrAlias("date"));
        assertEquals(Command.EXIT, Command.getByNameOrAlias("exit"));
        assertEquals(Command.EXIT, Command.getByNameOrAlias("quit"));
        assertEquals(Command.HELP, Command.getByNameOrAlias("help"));
        assertEquals(Command.RESET, Command.getByNameOrAlias("reset"));
        assertEquals(Command.RUN, Command.getByNameOrAlias("run"));
        assertEquals(Command.SHOW_AGENTS, Command.getByNameOrAlias("show-agents"));
        assertEquals(Command.SHOW_AGENTS, Command.getByNameOrAlias("agents"));
        assertEquals(Command.SHOW_THREADS, Command.getByNameOrAlias("show-threads"));
        assertEquals(Command.SHOW_THREADS, Command.getByNameOrAlias("threads"));
        assertEquals(Command.START, Command.getByNameOrAlias("start"));
        assertEquals(Command.STATUS, Command.getByNameOrAlias("status"));
        assertEquals(Command.STOP, Command.getByNameOrAlias("stop"));
        assertEquals(Command.UPTIME, Command.getByNameOrAlias("uptime"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommandsByNameOrAliasWithInvalidName()
    {
        Command.getByNameOrAlias("x");
    }
    
    @Test
    public void testCommandsByNameOrAliasOrNullWithInvalidName()
    {
        assertNull(Command.getByNameOrAliasOrNull("x"));
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testCommandsByNameOrAliasWithEmptyAlias()
    {
        Command.getByNameOrAlias("");
    }
    
    @Test
    public void testCommandNameAndAlias()
    {
        assertEquals("show-agents", Command.SHOW_AGENTS.getName());
        assertEquals("agents", Command.SHOW_AGENTS.getAlias());
    }
}
