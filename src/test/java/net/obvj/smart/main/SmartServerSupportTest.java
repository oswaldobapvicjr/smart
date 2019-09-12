package net.obvj.smart.main;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.conf.SmartProperties;
import net.obvj.smart.console.ManagementConsole;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SmartProperties.class, ManagementConsole.class })
public class SmartServerSupportTest
{
    private SmartProperties properties;
    private ManagementConsole console;

    // Test subject
    private SmartServerSupport support = new SmartServerSupport();

    @Before
    public void setup()
    {
        properties = mock(SmartProperties.class);
        mockStatic(SmartProperties.class);
        when(SmartProperties.getInstance()).thenReturn(properties);

        console = mock(ManagementConsole.class);
        mockStatic(ManagementConsole.class);
        when(ManagementConsole.getInstance()).thenReturn(console);
    }

    @Test
    public void testStartClassicManagementConsoleEnabled()
    {
        when(properties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED)).thenReturn(true);
        support.startClassicManagementConsole();
        verify(console).start();
    }

    @Test
    public void testStartClassicManagementConsoleDisabled()
    {
        when(properties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED)).thenReturn(false);
        support.startClassicManagementConsole();
        verify(console, never()).start();
    }
    
    @Test
    public void testCloseClassicManagementConsoleEnabled()
    {
        when(properties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED)).thenReturn(true);
        support.closeClassicManagementConsole();
        verify(console).stop();
    }

    @Test
    public void testCloseClassicManagementConsoleDisabled()
    {
        when(properties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED)).thenReturn(false);
        support.closeClassicManagementConsole();
        verify(console, never()).stop();
    }

}
