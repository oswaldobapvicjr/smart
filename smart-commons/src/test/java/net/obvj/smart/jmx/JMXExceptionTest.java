package net.obvj.smart.jmx;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Unit test for the {@link JMXException} class.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class JMXExceptionTest
{
    private static final String DETAILED_MESSAGE1 = "detailedMessage1";
    private static final String ROOT_CAUSE_MESSAGE1 = "rootCauseMessage1";

    @Test
    public void testWithDetailedMessage()
    {
        try
        {
            throw new JMXException(DETAILED_MESSAGE1);
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
            throw new JMXException(new IllegalArgumentException(ROOT_CAUSE_MESSAGE1));
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
            throw new JMXException(DETAILED_MESSAGE1, new IllegalArgumentException(ROOT_CAUSE_MESSAGE1));
        }
        catch (Throwable throwable)
        {
            assertEquals(DETAILED_MESSAGE1, throwable.getMessage());
            assertEquals(IllegalArgumentException.class, throwable.getCause().getClass());
        }
    }

}
