package net.obvj.smart.console;

import static net.obvj.smart.console.Command.getByNameOrAlias;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.conf.SmartProperties;
import net.obvj.smart.util.ConsoleUtil;

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

    protected static final String PROMPT = SmartProperties.getInstance().getProperty(SmartProperties.CONSOLE_PROMPT) + " ";

    protected static final List<String> HINTS = Arrays.asList(" Type 'help' for a list of available commands.",
            " Type 'exit' to quit the console.");

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final Logger log = Logger.getLogger(this.getClass().getName());

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
        sendLines(ConsoleUtil.readCustomHeaderLines());
        sendLine();
        sendLines(HINTS);
    }
    
    public void run()
    {
        try
        {
            printCustomHeader();
            while (true)
            {
                sendLine();
                send(PROMPT);
                String commandLine = readLine();

                if (!"".equals(commandLine))
                {
                    log.log(Level.INFO, "Command received: {0}", commandLine);
                    
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
            log.info(MSG_SESSION_CLOSED_DUE_TO_INACTIVITY);
            sendLine(LINE_SEPARATOR + MSG_SESSION_CLOSED_DUE_TO_INACTIVITY);
        }
        catch (Exception e)
        {
            log.severe(e.getClass().getName() + ": " + e.getMessage());
        }
        finally
        {
            log.info(MSG_CLOSING_CONSOLE_SESSION);
            sendLine(MSG_CLOSING_CONSOLE_SESSION);
            if (socket != null)
            {
                try
                {
                    socket.close();
                }
                catch (IOException e)
                {
                    log.severe("Error closing client socket connected to port " + ManagementConsole.getPort());
                }
            }
        }
    }

    protected void handleUserInput(String[] arguments)
    {
        Command command = getByNameOrAlias(arguments[0]);
        command.execute(arguments, out);
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
        lines.forEach(line -> out.println(line));
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

}
