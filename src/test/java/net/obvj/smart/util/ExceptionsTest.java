package net.obvj.smart.util;

import org.junit.Test;

import net.obvj.smart.conf.AgentConfigurationException;

import static net.obvj.smart.TestUtil.*;

/**
 * Unit tests for the {@link Exceptions} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ExceptionsTest
{
    private static String MSG_PATTERN = "arg1=%s,arg2=%s";
    private static String ARG1 = "abc";
    private static String ARG2 = "123";
    private static String EXPECTED_MSG = "arg1=abc,arg2=123";

    @Test
    public void testNoInstances() throws Exception
    {
        checkNoInstancesAllowed(Exceptions.class, IllegalStateException.class, "Utility class");
    }

    @Test
    public void testIllegalArgumentWithMessageAndParams()
    {
        assertException(IllegalArgumentException.class, EXPECTED_MSG,
                Exceptions.illegalArgument(MSG_PATTERN, ARG1, ARG2));
    }

    @Test
    public void testIllegalArgumentWithMessageAndParamsAndCause()
    {
        assertException(IllegalArgumentException.class, EXPECTED_MSG, NullPointerException.class,
                Exceptions.illegalArgument(new NullPointerException(), MSG_PATTERN, ARG1, ARG2));
    }

    @Test
    public void testIllegalStateWithMessageAndParams()
    {
        assertException(IllegalStateException.class, EXPECTED_MSG, Exceptions.illegalState(MSG_PATTERN, ARG1, ARG2));
    }

    @Test
    public void testIllegalStateWithMessageAndParamsAndCause()
    {
        assertException(IllegalStateException.class, EXPECTED_MSG, NullPointerException.class,
                Exceptions.illegalState(new NullPointerException(), MSG_PATTERN, ARG1, ARG2));
    }
    
    @Test
    public void testAgentConfigurationWithMessageAndParamsAndCause()
    {
        assertException(AgentConfigurationException.class, EXPECTED_MSG, NullPointerException.class,
                Exceptions.agentConfiguration(new NullPointerException(), MSG_PATTERN, ARG1, ARG2));
    }

}
