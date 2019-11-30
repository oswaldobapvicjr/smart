package net.obvj.smart.conf.properties;

import static net.obvj.smart.conf.properties.SmartProperties.*;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.conf.properties.SmartProperties;

/**
 * Unit tests for the {@link SmartProperties} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(SmartProperties.class)
public class SmartPropertiesMockedTest
{
    /**
     * Tests that the default value for a string property is retrieved in case of an
     * IOException reading the properties file
     */
    @Test
    public void testDefaultConsolePromptDueToIOException()
    {
        // Mocking only the readPropertiesFile method
        PowerMockito.stub(PowerMockito.method(SmartProperties.class, "readPropertiesFile", String.class))
                .toThrow(new IOException("test"));

        SmartProperties smart = new SmartProperties("testProperties/smart-consolePrompt1.properties");
        assertEquals(0, smart.properties.size());
        assertEquals(CONSOLE_PROMPT_DEFAULT, smart.getProperty(CONSOLE_PROMPT));
    }

}
