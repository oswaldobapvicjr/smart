package net.obvj.smart.console.enhanced.commands;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import jline.console.ConsoleReader;

/**
 * Unit tests for the {@link ClearScreenCommand} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
public class ClearScreenCommandTest
{
    @Mock
    Commands commands;
    @Mock
    ConsoleReader reader;
    
    @InjectMocks
    ClearScreenCommand command;

    @Test
    public void testCall() throws IOException 
    {
        Mockito.when(commands.getConsoleReader()).thenReturn(reader);
        command.call();
        Mockito.verify(reader).clearScreen();        
    }

}
