package net.obvj.smart.console.enhanced;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import net.obvj.smart.console.enhanced.EnhancedManagementConsole.Mode;

public class EnhancedManagementConsoleTest
{

    @Test
    public void testParseArgs()
    {
        assertEquals("start DummyAgent", EnhancedManagementConsole.parseArgs("start", "DummyAgent"));
        assertEquals("agents", EnhancedManagementConsole.parseArgs("agents"));
        assertEquals("", EnhancedManagementConsole.parseArgs());
    }

    @Test
    public void testAssignableMode() throws IOException
    {
        assertEquals(Mode.INTERACTIVE, new EnhancedManagementConsole().getMode());
        assertEquals(Mode.INTERACTIVE, new EnhancedManagementConsole("").getMode());
        assertEquals(Mode.SINGLE_COMMAND, new EnhancedManagementConsole("agents").getMode());
    }

    
}
