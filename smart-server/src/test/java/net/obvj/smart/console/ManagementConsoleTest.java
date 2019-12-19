package net.obvj.smart.console;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.smart.conf.properties.SmartProperties;

@RunWith(MockitoJUnitRunner.class)
public class ManagementConsoleTest
{
    @Mock
    private SmartProperties properties;
    
    @InjectMocks
    private ManagementConsole console;
    
    @Test
    public void testGetPort()
    {
        Mockito.when(properties.getIntProperty(SmartProperties.CLASSIC_CONSOLE_PORT)).thenReturn(4000);
        assertEquals(4000, console.getPort());
    }
    
    @Test
    public void testGetTimeout()
    {
        Mockito.when(properties.getIntProperty(SmartProperties.CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS)).thenReturn(99);
        assertEquals(99, console.getSessionTimeoutSeconds());
    }

}
