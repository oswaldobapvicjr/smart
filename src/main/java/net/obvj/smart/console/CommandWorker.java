package net.obvj.smart.console;

import static net.obvj.smart.console.Command.EXIT;
import static net.obvj.smart.console.Command.getByNameOrAlias;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.conf.SmartProperties;

/**
 * A Runnable object for handling of user commands via classic Management Console
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class CommandWorker implements Runnable
{

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String PROMPT = SmartProperties.getInstance().getProperty(SmartProperties.CONSOLE_PROMPT) + " ";

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

    private void printCustomHeader() throws IOException
    {
        log.fine("Searching for custom header file...");
        try
        {
            URL headerFileURL = CommandWorker.class.getClassLoader().getResource("header.txt");
            if (headerFileURL == null)
            {
                log.warning("Unable to find header.txt file");
                return;
            }
            sendLines(Files.readAllLines(Paths.get(headerFileURL.toURI())));
        }
        catch (URISyntaxException e)
        {
            log.warning("Unable to find header.txt file");
        }
    }

    private void printHints()
    {
        out.println();
        out.println(" Type 'help' for a list of available commands.");
        out.println(" Type 'exit' to quit the console.");
    }
    
    public void run()
    {
        try
        {
            printCustomHeader();
            printHints();
            while (true)
            {
                out.println();
                send(PROMPT);
                String commandLine = readLine();

                if (!"".equals(commandLine))
                {
                    log.log(Level.INFO, "Command received: {0}", commandLine);
                    
                    String[] arguments = commandLine.split(" ");
                    String command = arguments[0];

                    if (EXIT == Command.getByNameOrAliasOrNull(command))
                    {
                        return;
                    }
                    else
                    {
                        handleUserInput(arguments);
                    }
                }
            }
        }
        catch (SocketTimeoutException e)
        {
            log.info("Sesion closed due to inactivity.");
            sendLine(LINE_SEPARATOR + "Session timed-out due to inactivity.");
        }
        catch (Exception e)
        {
            log.severe(e.getClass().getName() + ": " + e.getMessage());
        }
        finally
        {
            if (socket != null)
            {
                String closingMessage = "Closing console session...";
                sendLine(closingMessage);
                log.info(closingMessage);
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

    private void handleUserInput(String[] arguments)
    {
        try
        {
            Command command = getByNameOrAlias(arguments[0]);
            command.execute(arguments, out);
        }
        catch (IllegalArgumentException ile)
        {
            sendLine(ile.getMessage());
        }
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

    private String readLine() throws IOException
    {
        return sanitizeUserInput(in.readLine());
    }

    private String sanitizeUserInput(String source)
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

    private String eraseBackspaces(String source)
    {
        String str = source;
        while (str.contains("\b"))
        {
            str = str.replaceAll("^\b+|[^\b]\b", "");
        }
        return str;
    }

}
