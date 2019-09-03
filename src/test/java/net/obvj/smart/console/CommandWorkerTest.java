package net.obvj.smart.console;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.util.DateUtil;

/**
 * Unit tests for the {@link CommandWorker} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class CommandWorkerTest
{
    private static final String STR_TEST_DATE = "2019-09-03 17:03:06";

    /**
     * Creates a new command worker that will print onto the given StringWriter
     */
    private CommandWorker newCommandWorker(StringWriter out) throws IOException
    {
        return new CommandWorker(new PrintWriter(out));
    }

    @Test
    public void testSanitizeInputWithSeveralStrings()
    {
        assertEquals("start DummyAgent", CommandWorker.sanitizeUserInput("start DummyAgent "));
        assertEquals("show-threads", CommandWorker.sanitizeUserInput("show-agents\b\b\b\b\b\bthreads"));
        assertEquals("status DummyAgent", CommandWorker.sanitizeUserInput("sta\ttus DummyAgent   "));
        assertEquals("", CommandWorker.sanitizeUserInput(""));
        assertEquals("", CommandWorker.sanitizeUserInput(null));
    }
    
    @Test
    public void testHandleLine() throws IOException
    {
        PowerMockito.mockStatic(DateUtil.class);
        PowerMockito.when(DateUtil.now()).thenReturn(STR_TEST_DATE);
        
        StringWriter out = new StringWriter();
        CommandWorker worker = newCommandWorker(out);
        worker.handleUserInput(new String[] { "date" });
        assertEquals(STR_TEST_DATE, out.toString().trim());
    }

}
