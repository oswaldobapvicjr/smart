package net.obvj.smart;

import org.junit.Test;

/**
 * Unit tests for the {@link TestUtil} class.
 * 
 * @author oswaldo.bapvic.jr
 */
public class TestUtilTest
{

    @Test
    public void testAssertExceptionWithThrowableAndExpectedClass()
    {
        TestUtil.assertException(IllegalArgumentException.class, new IllegalArgumentException("message1"));
    }

    @Test
    public void testAssertExceptionWithThrowableAndExpectedClassAndMessage()
    {
        TestUtil.assertException(IllegalArgumentException.class, "message1", new IllegalArgumentException("message1"));
    }

    @Test
    public void testAssertExceptionWithThrowableAndExpectedClassAndMessageAndCause()
    {
        TestUtil.assertException(IllegalArgumentException.class, "message1", NullPointerException.class,
                new IllegalArgumentException("message1", new NullPointerException()));
    }

    @Test(expected = AssertionError.class)
    public void testAssertExceptionWithThrowableAndExpectedClassInvalid()
    {
        TestUtil.assertException(IllegalStateException.class, new IllegalArgumentException("message1"));
    }

}
