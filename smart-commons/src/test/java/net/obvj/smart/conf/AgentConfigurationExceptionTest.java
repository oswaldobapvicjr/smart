package net.obvj.smart.conf;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test for the {@link AgentConfigurationException} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentConfigurationExceptionTest
{
    private static final String DETAILED_MESSAGE1 = "detailedMessage1";
    private static final String ROOT_CAUSE_MESSAGE1 = "rootCauseMessage1";

    @Test
    public void testWithDetailedMessage()
    {
        try
        {
            throw new AgentConfigurationException(DETAILED_MESSAGE1);
        }
        catch (Throwable throwable)
        {
            assertEquals(DETAILED_MESSAGE1, throwable.getMessage());
            assertNull(throwable.getCause());
        }
    }

    @Test
    public void testWithCause()
    {
        try
        {
            throw new AgentConfigurationException(new IllegalArgumentException(ROOT_CAUSE_MESSAGE1));
        }
        catch (Throwable throwable)
        {
            assertTrue(throwable.getMessage().endsWith(ROOT_CAUSE_MESSAGE1));
            assertEquals(IllegalArgumentException.class, throwable.getCause().getClass());
        }
    }

    @Test
    public void testWithDetailedMessageAndCause()
    {
        try
        {
            throw new AgentConfigurationException(DETAILED_MESSAGE1,
                    new IllegalArgumentException(ROOT_CAUSE_MESSAGE1));
        }
        catch (Throwable throwable)
        {
            assertEquals(DETAILED_MESSAGE1, throwable.getMessage());
            assertEquals(IllegalArgumentException.class, throwable.getCause().getClass());
        }
    }

}
