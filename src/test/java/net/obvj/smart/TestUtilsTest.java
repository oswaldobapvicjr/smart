package net.obvj.smart;

import org.junit.Test;

/**
 * Unit tests for the {@link TestUtils} class.
 * 
 * @author oswaldo.bapvic.jr
 */
public class TestUtilsTest
{
    private static final String MESSAGE1 = "message1";
    private static final IllegalArgumentException ILLEGAL_ARGUMENT_EXCEPTION = new IllegalArgumentException(MESSAGE1,
            new NullPointerException());

    @Test
    public void testAssertExceptionWithThrowableAndExpectedClass()
    {
        TestUtils.assertException(IllegalArgumentException.class, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    public void testAssertExceptionWithThrowableAndExpectedClassAndMessage()
    {
        TestUtils.assertException(IllegalArgumentException.class, MESSAGE1, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test
    public void testAssertExceptionWithThrowableAndExpectedClassAndMessageAndCause()
    {
        TestUtils.assertException(IllegalArgumentException.class, MESSAGE1, NullPointerException.class,
                ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test(expected = AssertionError.class)
    public void testAssertExceptionWithInvalidThrowable()
    {
        TestUtils.assertException(IllegalStateException.class, ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test(expected = AssertionError.class)
    public void testAssertExceptionWithInvalidMessage()
    {
        TestUtils.assertException(IllegalArgumentException.class, "message2", ILLEGAL_ARGUMENT_EXCEPTION);
    }

    @Test(expected = AssertionError.class)
    public void testAssertExceptionWithInvalidCause()
    {
        TestUtils.assertException(IllegalArgumentException.class, MESSAGE1, IllegalStateException.class,
                ILLEGAL_ARGUMENT_EXCEPTION);
    }

}
