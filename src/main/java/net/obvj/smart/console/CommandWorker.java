package net.obvj.smart.console;

import static net.obvj.smart.console.Command.*;

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
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import net.obvj.smart.manager.AgentManager;

/**
 * A Runnable object for handling of user commands via Management Console
 */
public class CommandWorker implements Runnable
{

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final String CMD_PROMPT = LINE_SEPARATOR + "smart> ";

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private AgentManager manager;

    private final Logger log = Logger.getLogger(this.getClass().getName());

    public CommandWorker(Socket socket) throws IOException
    {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.out = new PrintWriter(this.socket.getOutputStream(), true);
        this.manager = AgentManager.getInstance();
    }

    private void printHeader() throws IOException
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

    public void run()
    {
        try
        {
            printHeader();
            while (true)
            {
                send(CMD_PROMPT);
                String commandLine = readLine();
                if (commandLine == null)
                {
                    return;
                }
                commandLine = commandLine.trim();
                if (!commandLine.equals(""))
                {
                    String[] arguments = commandLine.split(" ");
                    String command = arguments[0];

                    if (command.equalsIgnoreCase(EXIT.getString()))
                    {
                        return;
                    }
                    else if (command.equalsIgnoreCase(STOP.getString()))
                    {
                        if (arguments.length >= 2)
                        {
                            log.info("Command received: " + commandLine);
                            stopAgent(arguments[1]);
                        }
                        else
                        {
                            sendLine("Missing parameter: <agent-class>");
                        }

                    }
                    else if (command.equalsIgnoreCase(STATUS.getString()))
                    {
                        if (arguments.length >= 2)
                        {
                            status(arguments[1]);
                        }
                        else
                        {
                            sendLine("Missing parameter: <agent-class>");
                        }

                    }
                    else if (command.equalsIgnoreCase(RESET.getString()))
                    {
                        if (arguments.length >= 2)
                        {
                            log.info("Command received: " + commandLine);
                            resetAgent(arguments[1]);
                        }
                        else
                        {
                            sendLine("Missing parameter: <agent-class>");
                        }

                    }
                    else
                    {
                        handleUserInput(arguments);
                    }
                }
            } // EO while

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
                log.info("Closing console session...");
                try
                {
                    socket.close();
                }
                catch (IOException e)
                {
                    log.severe("Error closing client socket connected to port " + ManagementConsole.PORT);
                }
            }
        }
    }

    private void handleUserInput(String[] arguments)
    {
        try
        {
            Command command = getCommandByString(arguments[0]);
            command.execute(arguments, out);
        }
        catch (IllegalArgumentException ile)
        {
            sendLine(ile.getMessage());
        }
    }

    private void stopAgent(String agent)
    {
        if (agent == null || agent.equals(""))
        {
            sendLine("Missing parameter: <agent-class>");
        }
        else
        {
            String message = String.format("Stopping %s...", agent);
            sendLine(message);
            log.info(message);

            try
            {
                manager.stopAgent(agent);
                sendLine("Success.");
                sendLine("");
                status(agent);
            }
            catch (IllegalStateException e)
            {
                log.warning("Illegal state: " + e.getMessage());
                sendLine(e.getMessage());
                sendLine("");
                status(agent);
            }
            catch (IllegalArgumentException e)
            {
                log.warning(e.getMessage());
                sendLine(e.getMessage());
            }
            catch (TimeoutException ex)
            {
                String errMessage = String.format("Timeout waiting for agent task to complete: %s", agent);
                sendLine(errMessage);
                log.warning(errMessage);
                sendLine("");
                status(agent);
            }
            catch (UnsupportedOperationException e)
            {
                String errMessage = String.format("Unsupported operation: %s", e.getMessage());
                sendLine(errMessage);
                log.warning(errMessage);
                sendLine("");
                status(agent);
            }
        }
    }

    private void resetAgent(String agent)
    {
        if (agent == null || agent.equals(""))
        {
            sendLine("Missing parameter: <agent-class>");
        }
        else
        {
            String message = String.format("Resetting %s...", agent);
            sendLine(message);
            log.info(message);

            try
            {
                manager.resetAgent(agent);
                sendLine("Success.");
                sendLine("");
                status(agent);
            }
            catch (IllegalStateException e)
            {
                log.warning("Illegal state: " + e.getMessage());
                sendLine(e.getMessage());
                sendLine("");
                status(agent);
            }
            catch (IllegalArgumentException e)
            {
                log.warning(e.getMessage());
                sendLine(e.getMessage());
            }
        }
    }

    private void status(String agent)
    {
        if (agent == null || agent.equals(""))
        {
            sendLine("Missing parameter: <agent-class>");
        }
        else
        {
            try
            {
                sendLine(manager.getAgentStatusStr(agent));
            }
            catch (IllegalArgumentException e)
            {
                log.warning(e.getMessage());
                sendLine(e.getMessage());
            }
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
        return in.readLine();
    }

}
