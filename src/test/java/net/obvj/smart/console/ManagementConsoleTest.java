package net.obvj.smart.console;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.conf.SmartProperties;

/**
 * Unit tests for operations inside {@link ManagementConsole}.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SmartProperties.class })
public class ManagementConsoleTest
{

    private SmartProperties properties;

    @Before
    public void setup()
    {
        properties = mock(SmartProperties.class);
        mockStatic(SmartProperties.class);
        when(SmartProperties.getInstance()).thenReturn(properties);
    }

    @Test
    public void testPort()
    {
        when(properties.getIntProperty(SmartProperties.CLASSIC_CONSOLE_PORT)).thenReturn(888);
        assertEquals(888, ManagementConsole.getPort());
    }
    
    @Test
    public void testSessionTimeout()
    {
        when(properties.getIntProperty(SmartProperties.CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS)).thenReturn(123);
        assertEquals(123, ManagementConsole.getSessionTimeoutSeconds());
    }

}
