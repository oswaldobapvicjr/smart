package net.obvj.smart.conf;

import static net.obvj.smart.conf.SmartProperties.*;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for the {@link SmartProperties} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class SmartPropertiesTest
{
    private static final String UNKNOWN_FILE = "unknownFile";

    /**
     * Tests that the string property from the file is retrieved
     */
    @Test
    public void testGetCustomConsolePrompt()
    {
        SmartProperties smart = new SmartProperties("testProperties/smart-consolePrompt1.properties");
        assertEquals(1, smart.properties.size());
        assertEquals("consolePrompt1", smart.getProperty(CONSOLE_PROMPT));
    }

    /**
     * Tests that the default value for a string property is retrieved when the file or
     * property is not found
     */
    @Test
    public void testDefaultConsolePrompt()
    {
        SmartProperties smart = new SmartProperties(UNKNOWN_FILE);
        assertEquals(0, smart.properties.size());
        assertEquals(CONSOLE_PROMPT_DEFAULT, smart.getProperty(CONSOLE_PROMPT));
    }

    /**
     * Tests that an integer property from the file is retrieved
     */
    @Test
    public void testGetCustomClassicConsolePort()
    {
        SmartProperties smart = new SmartProperties("testProperties/smart-classicConsolePort9999.properties");
        assertEquals(1, smart.properties.size());
        assertEquals(9999, smart.getIntProperty(CLASSIC_CONSOLE_PORT));
    }

    /**
     * Tests that the default value for an integer property is retrieved when the file or
     * property is not found
     */
    @Test
    public void testDefaultClassicConsolePort()
    {
        SmartProperties smart = new SmartProperties(UNKNOWN_FILE);
        assertEquals(0, smart.properties.size());
        assertEquals(CLASSIC_CONSOLE_PORT_DEFAULT, smart.getIntProperty(CLASSIC_CONSOLE_PORT) + "");
    }

    /**
     * Tests that a boolean property from the file is retrieved
     */
    @Test
    public void testGetCustomClassicConsoleEnabled()
    {
        SmartProperties smart = new SmartProperties("testProperties/smart-classicConsoleEnabledFalse.properties");
        assertEquals(1, smart.properties.size());
        assertEquals(false, smart.getBooleanProperty(CLASSIC_CONSOLE_ENABLED));
    }

    /**
     * Tests that the default value for a boolean property is retrieved when the file or
     * property is not found
     */
    @Test
    public void testDefaultClassicConsoleEnabled()
    {
        SmartProperties smart = new SmartProperties(UNKNOWN_FILE);
        assertEquals(0, smart.properties.size());
        assertEquals(CLASSIC_CONSOLE_ENABLED_DEFAULT, smart.getBooleanProperty(CLASSIC_CONSOLE_ENABLED) + "");
    }

}
