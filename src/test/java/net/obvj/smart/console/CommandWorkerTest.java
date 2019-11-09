package net.obvj.smart.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.util.ConsoleUtil;
import net.obvj.smart.util.DateUtil;

/**
 * Unit tests for the {@link CommandWorker} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DateUtil.class, ConsoleUtil.class })
public class CommandWorkerTest
{
    private static final String STR_TEST_DATE = "2019-09-03 17:03:06";

    @Mock
    private Socket socket;
    @Mock
    private InputStream in;
    @Mock
    private OutputStream out;

    @Before
    public void setup() throws IOException
    {
        Mockito.when(socket.getInputStream()).thenReturn(in);
        Mockito.when(socket.getOutputStream()).thenReturn(out);
    }

    /**
     * Creates a new command worker that will read from and print onto the given StringReader
     * and StringWriter objects
     */
    private CommandWorker newCommandWorker(StringReader in, StringWriter out)
    {
        return new CommandWorker(in != null ? new BufferedReader(in) : null, out != null ? new PrintWriter(out) : null);
    }

    @Test
    public void testConstructor() throws IOException
    {
        CommandWorker worker = new CommandWorker(socket);
        Mockito.verify(socket).getInputStream();
        Mockito.verify(socket).getOutputStream();
        assertNotNull(worker.getInputStream());
        assertNotNull(worker.getOutputStream());
    }

    @Test
    public void testSanitizeInput()
    {
        assertEquals("start DummyAgent", CommandWorker.sanitizeUserInput("start DummyAgent "));
        assertEquals("show-threads", CommandWorker.sanitizeUserInput("show-agents\b\b\b\b\b\bthreads"));
        assertEquals("status DummyAgent", CommandWorker.sanitizeUserInput("sta\ttus DummyAgent   "));
        assertEquals("", CommandWorker.sanitizeUserInput(""));
        assertEquals("", CommandWorker.sanitizeUserInput(null));
    }

    @Test
    public void testReadLineSanitized() throws IOException
    {
        StringReader in = new StringReader("a\bshow-\t\tagents  ");
        CommandWorker worker = newCommandWorker(in, null);
        assertEquals("show-agents", worker.readLine());
    }

    @Test
    public void testHandleLine()
    {
        PowerMockito.mockStatic(DateUtil.class);
        PowerMockito.when(DateUtil.now()).thenReturn(STR_TEST_DATE);

        StringWriter out = new StringWriter();
        CommandWorker worker = newCommandWorker(null, out);
        worker.handleUserInput(new String[] { "date" });
        assertEquals(STR_TEST_DATE, out.toString().trim());
    }

    @Test
    public void testSendLine()
    {
        StringWriter out = new StringWriter();
        CommandWorker worker = newCommandWorker(null, out);
        worker.sendLine();
        assertEquals(CommandWorker.LINE_SEPARATOR, out.toString());
    }

    @Test
    public void testSendString()
    {
        StringWriter out = new StringWriter();
        CommandWorker worker = newCommandWorker(null, out);
        worker.send("str1");
        assertEquals("str1", out.toString());
    }

    @Test
    public void testSendStringLine()
    {
        StringWriter out = new StringWriter();
        CommandWorker worker = newCommandWorker(null, out);
        worker.sendLine("line1");
        assertEquals("line1" + CommandWorker.LINE_SEPARATOR, out.toString());
    }

    @Test
    public void testSendLines()
    {
        StringWriter out = new StringWriter();
        CommandWorker worker = newCommandWorker(null, out);
        worker.sendLines(Arrays.asList("line1", "line2"));
        assertTrue(out.toString().contains("line1" + CommandWorker.LINE_SEPARATOR + "line2"));
    }

    @Test
    public void testCustomHeader()
    {
        PowerMockito.mockStatic(ConsoleUtil.class);
        List<String> mockedHeaderLines = Arrays.asList("Header line 1", "Header line 2");
        PowerMockito.when(ConsoleUtil.readCustomHeaderLines()).thenReturn(mockedHeaderLines);

        StringWriter out = new StringWriter();
        CommandWorker worker = newCommandWorker(null, out);
        worker.printCustomHeader();

        String header = out.toString();
        mockedHeaderLines.forEach(line -> assertTrue(header.contains(line)));
        CommandWorker.HINTS.forEach(line -> assertTrue(header.contains(line)));
    }

    @Test
    public void testHandleExit()
    {
        StringReader in = new StringReader("exit");
        StringWriter out = new StringWriter();
        newCommandWorker(in, out).run();
        assertTrue(
                String.format("Expected ending \"%s\" not found in string:%n%s",
                        CommandWorker.MSG_CLOSING_CONSOLE_SESSION, out.toString()),
                out.toString().trim().endsWith(CommandWorker.MSG_CLOSING_CONSOLE_SESSION));
    }

    @Test
    public void testHandleUserInputWithInvalidCommand()
    {
        StringWriter out = new StringWriter();
        CommandWorker worker = newCommandWorker(null, out);
        worker.handleUserInput(new String[] {"invalidcom"});
        assertTrue(out.toString().trim().contains("Invalid command: invalidcom"));
    }

}
