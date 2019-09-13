package net.obvj.smart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

/**
 * Common utilities for working with unit tests.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class TestUtil
{
    private static final String EXPECTED_BUT_NOT_THROWN = "Expected but not thrown: \"%s\"";

    /**
     * A utility function that accepts no argument and returns void, to allow testing methods
     * with this type of signature (e.g., a Runnable's {@code run()} method).
     */
    @FunctionalInterface
    public interface Procedure
    {
        void execute();
    }

    /**
     * Tests that no instances of an utility class are created.
     *
     * @param utilityClass           the class subject to test
     * @param expectedThrowableClass the expected throwable to be checked in case the
     *                               constructor is called
     * @param expectedErrorMessage   the expected error message to be checked in case the
     *                               constructor is called
     * @throws Exception in case of errors getting constructor metadata or instantiating the
     *                   private constructor via Reflection
     */
    public static void testNoInstancesAllowed(Class<?> utilityClass, Class<? extends Throwable> expectedThrowableClass,
            String expectedErrorMessage) throws Exception
    {
        try
        {
            Constructor<?> constructor = utilityClass.getDeclaredConstructor();
            assertTrue("Constructor is not private", Modifier.isPrivate(constructor.getModifiers()));
            constructor.setAccessible(true);
            constructor.newInstance();
            fail("Class was instantiated");
        }
        catch (InvocationTargetException ite)
        {
            Throwable cause = ite.getCause();
            assertEquals(expectedThrowableClass, cause.getClass());
            assertEquals(expectedErrorMessage, cause.getMessage());
        }
    }

    /**
     * A utility method to assert that a given throwable matches the expected class, cause and
     * message.
     * 
     * @param expectedThrowable the expected throwable class
     * @param expectedMessage   the expected message (if applicable)
     * @param expectedCause     the expected throwable cause class (if applicable)
     * @param throwable         the throwable to be validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Class<? extends Throwable> expectedCause, Throwable throwable)
    {
        assertEquals(expectedThrowable, throwable.getClass());
        if (expectedMessage != null) assertEquals(expectedMessage, throwable.getMessage());
        if (expectedCause != null) assertEquals(expectedCause, throwable.getCause().getClass());
    }

    /**
     * A utility method to assert the expected exception, message and cause thrown by a
     * supplying function.
     * <p>
     * Example of usage:
     * <p>
     * <code>
     * assertException(AgentConfigurationException.class, "Invalid agents file", UnmarshalException.class,
     *           () -> AgentConfiguration.loadAgentsXmlFile("testAgents/timerAgentWithoutName.xml"))
     * </code>
     * 
     * @param expectedThrowable the expected throwable class
     * @param expectedMessage   the expected message (if applicable)
     * @param expectedCause     the expected throwable cause class (if applicable)
     * @param supplier          the supplying function that throws an exception to be
     *                          validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Class<? extends Throwable> expectedCause, Supplier<?> supplier)
    {
        try
        {
            supplier.get();
        }
        catch (Throwable throwable)
        {
            assertException(expectedThrowable, expectedMessage, expectedCause, throwable);
            return;
        }
        fail(String.format(EXPECTED_BUT_NOT_THROWN, expectedThrowable));
    }

    /**
     * A utility method to assert the expected exception, message and cause thrown by a given
     * procedure, that is, a function that accepts no arguments and returns void (e.g., a
     * Runnable's {@code run()} method).
     * <p>
     * Example of usage:
     * <p>
     * <code>
     * assertException(IllegalStateException.class, "Agent already started", null,
     *           () -> agent.start())
     * </code>
     * 
     * @param expectedThrowable the expected throwable class
     * @param expectedMessage   the expected message (if applicable)
     * @param expectedCause     the expected throwable cause class (if applicable)
     * @param procedure         the procedure that produces an exception to be * validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Class<? extends Throwable> expectedCause, Procedure procedure)
    {
        try
        {
            procedure.execute();
        }
        catch (Throwable throwable)
        {
            assertException(expectedThrowable, expectedMessage, expectedCause, throwable);
            return;
        }
        fail(String.format(EXPECTED_BUT_NOT_THROWN, expectedThrowable));
    }
}
