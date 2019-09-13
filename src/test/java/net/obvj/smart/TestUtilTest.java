package net.obvj.smart;

import org.junit.Test;

/**
 * Unit tests for the {@link TestUtil} class.
 * 
 * @author oswaldo.bapvic.jr
 */
public class TestUtilTest
{
    private static final String MESSAGE1 = "message1";
    private static final IllegalArgumentException ILLEGAL_ARGUMENT_EXCEPTION = new IllegalArgumentException(MESSAGE1,
            new NullPointerException());

    @Test
    public void testAssertExceptionWithThrowableAndExpectedClass()
    {
        TestUtil.assertException(IllegalArgumentException.class, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    public void testAssertExceptionWithThrowableAndExpectedClassAndMessage()
    {
        TestUtil.assertException(IllegalArgumentException.class, MESSAGE1, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    public void testAssertExceptionWithThrowableAndExpectedClassAndMessageAndCause()
    {
        TestUtil.assertException(IllegalArgumentException.class, MESSAGE1, NullPointerException.class,
                ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test(expected = AssertionError.class)
    public void testAssertExceptionWithInvalidThrowable()
    {
        TestUtil.assertException(IllegalStateException.class, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test(expected = AssertionError.class)
    public void testAssertExceptionWithInvalidMessage()
    {
        TestUtil.assertException(IllegalArgumentException.class, "message2", ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test(expected = AssertionError.class)
    public void testAssertExceptionWithInvalidCause()
    {
        TestUtil.assertException(IllegalArgumentException.class, MESSAGE1, IllegalStateException.class,
                ILLEGAL_ARGUMENT_EXCEPTION);
    }

}
