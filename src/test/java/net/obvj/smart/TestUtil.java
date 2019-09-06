package net.obvj.smart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import net.obvj.smart.conf.xml.XmlSmart;

/**
 * Common utilities for working with unit tests.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class TestUtil
{
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
     * A utility method to assert the expected throwable and cause classes thrown by a
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
     * @param expectedCause     the expected throwable cause class (if applicable)
     * @param expectedMessage   the expected message (if applicable)
     * @param supplier          the supplying function that produces an exception to be
     *                          validated
     */
    public static void assertException(Class<? extends Throwable> expectedThrowable, String expectedMessage,
            Class<? extends Throwable> expectedCause, Supplier<XmlSmart> supplier)
    {
        try
        {
            supplier.get();
        }
        catch (Throwable throwable)
        {
            assertEquals(expectedThrowable, throwable.getClass());
            if (expectedMessage != null) assertEquals(expectedMessage, throwable.getMessage());
            if (expectedCause != null) assertEquals(expectedCause, throwable.getCause().getClass());
        }
    }
}
