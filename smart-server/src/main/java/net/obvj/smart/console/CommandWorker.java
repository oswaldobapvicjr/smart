package net.obvj.smart.console;

import static net.obvj.smart.console.Command.getByNameOrAlias;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.obvj.smart.conf.properties.SmartProperties;
import net.obvj.smart.util.ApplicationContextFacade;
import net.obvj.smart.util.ConsoleUtils;

/**
 * A Runnable object for handling of user commands via classic Management Console
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class CommandWorker implements Runnable
{
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected static final String MSG_SESSION_CLOSED_DUE_TO_INACTIVITY = "Sesion closed due to inactivity.";
    protected static final String MSG_CLOSING_CONSOLE_SESSION = "Closing console session...";

    protected static String prompt = ApplicationContextFacade.getBean(SmartProperties.class)
            .getProperty(SmartProperties.CONSOLE_PROMPT) + " ";

    protected static final List<String> HINTS = Arrays.asList(" Type 'help' for a list of available commands.",
            " Type 'exit' to quit the console.");

    private static final Logger LOG = LoggerFactory.getLogger(CommandWorker.class);

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public CommandWorker(Socket socket) throws IOException
    {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
    }

    protected CommandWorker(BufferedReader in, PrintWriter out)
    {
        this.socket = null;
        this.in = in;
        this.out = out;
    }

    protected void printCustomHeader()
    {
        sendLines(ConsoleUtils.readCustomHeaderLines());
        sendLine();
        sendLines(HINTS);
    }

    @Override
    public void run()
    {
        try
        {
            printCustomHeader();
            while (true)
            {
                sendLine();
                send(prompt);
                String commandLine = readLine();

                if (!"".equals(commandLine))
                {
                    LOG.info("Command received: {}", commandLine);

                    String[] arguments = commandLine.split(" ");
                    String command = arguments[0];

                    if ("exit".equals(command))
                    {
                        return;
                    }
                    handleUserInput(arguments);
                }
            }
        }
        catch (SocketTimeoutException e)
        {
            LOG.info(MSG_SESSION_CLOSED_DUE_TO_INACTIVITY);
            sendLine(LINE_SEPARATOR + MSG_SESSION_CLOSED_DUE_TO_INACTIVITY);
        }
        catch (Exception exception)
        {
            LOG.error("{} : {}", exception.getClass().getName(), exception.getMessage(), exception);
        }
        finally
        {
            LOG.info(MSG_CLOSING_CONSOLE_SESSION);
            sendLine(MSG_CLOSING_CONSOLE_SESSION);
            if (socket != null)
            {
                try
                {
                    socket.close();
                }
                catch (IOException exception)
                {
                    LOG.error("Error closing client socket", exception);
                }
            }
        }
    }

    protected void handleUserInput(String[] arguments)
    {
        try
        {
            Command command = getByNameOrAlias(arguments[0]);
            command.execute(arguments, out);
        }
        catch (Exception exception)
        {
            out.println(exception.getMessage());
        }
    }

    public void sendLine()
    {
        out.println();
        out.flush();
    }

    public void sendLine(String message)
    {
        out.println(message);
        out.flush();
    }

    public void sendLines(List<String> lines)
    {
        lines.forEach(out::println);
        out.flush();
    }

    public void send(String message)
    {
        out.print(message);
        out.flush();
    }

    protected String readLine() throws IOException
    {
        return sanitizeUserInput(in.readLine());
    }

    protected static String sanitizeUserInput(String source)
    {
        String string = "";
        if (source != null)
        {
            string = source.trim();
            string = eraseBackspaces(string);
            string = string.replaceAll("\\p{Cntrl}", "");
        }
        return string;
    }

    protected static String eraseBackspaces(String source)
    {
        String str = source;
        while (str.contains("\b"))
        {
            str = str.replaceAll("^\b+|[^\b]\b", "");
        }
        return str;
    }

    protected BufferedReader getInputStream()
    {
        return in;
    }

    protected PrintWriter getOutputStream()
    {
        return out;
    }

}
