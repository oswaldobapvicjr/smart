package net.obvj.smart.console;

import static net.obvj.smart.console.Command.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ThreadInfo;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.agents.api.DaemonAgent;
import net.obvj.smart.agents.api.TimerAgent;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.DateUtil;
import net.obvj.smart.util.SystemUtil;

/**
 * A Runnable object for handling of user commands via Management Console
 */
public class CommandWorker implements Runnable
{

    public static final String newLine = System.getProperty("line.separator");
    public static final String CMD_PROMPT = newLine + "smart> ";
    public static final String showAgentFormat = "%-36s %-6s %-7s";
    public static final String showThreadFormat = "%-4d %-32s %-13s";
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private AgentManager manager = null;
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
            READ_COMMANDS: while (true)
            {
                send(CMD_PROMPT);
                String commandLine = readLine();
                if (commandLine == null)
                {
                    break READ_COMMANDS;

                }
                else if (!commandLine.equals(""))
                {
                    String[] commands = commandLine.trim().split(" ");
                    String command = commands[0];

                    if (command.equalsIgnoreCase(EXIT.getString()))
                    {
                        break READ_COMMANDS;

                    }
                    else if (command.equalsIgnoreCase(START.getString()))
                    {
                        if (commands.length >= 2)
                        {
                            log.info("Command received: " + commandLine);
                            startAgent(commands[1]);
                        }
                        else
                        {
                            sendLine("Missing parameter: <agent-class>");
                        }

                    }
                    else if (command.equalsIgnoreCase(RUN.getString()))
                    {
                        if (commands.length >= 2)
                        {
                            log.info("Command received: " + commandLine);
                            runAgent(commands[1]);
                        }
                        else
                        {
                            sendLine("Missing parameter: <agent-class>");
                        }

                    }
                    else if (command.equalsIgnoreCase(STOP.getString()))
                    {
                        if (commands.length >= 2)
                        {
                            log.info("Command received: " + commandLine);
                            stopAgent(commands[1]);
                        }
                        else
                        {
                            sendLine("Missing parameter: <agent-class>");
                        }

                    }
                    else if (command.equalsIgnoreCase(STATUS.getString()))
                    {
                        if (commands.length >= 2)
                        {
                            status(commands[1]);
                        }
                        else
                        {
                            sendLine("Missing parameter: <agent-class>");
                        }

                    }
                    else if (command.equalsIgnoreCase(RESET.getString()))
                    {
                        if (commands.length >= 2)
                        {
                            log.info("Command received: " + commandLine);
                            resetAgent(commands[1]);
                        }
                        else
                        {
                            sendLine("Missing parameter: <agent-class>");
                        }

                    }
                    else if (command.equalsIgnoreCase(SHOW_AGENTS.getString()))
                    {
                        showAgents();

                    }
                    else if (command.equalsIgnoreCase(SHOW_THREADS.getString()))
                    {
                        showThreads();

                    }
                    else if (command.equalsIgnoreCase(DATE.getString()))
                    {
                        sendLine(DateUtil.now());

                    }
                    else if (command.equalsIgnoreCase(HELP.getString()))
                    {
                        sendLine("Available commands: ");
                        for (Command cmd : Command.values())
                        {
                            sendLine("   " + cmd.getString());
                        }

                    }
                    else
                    {
                        sendLine("'" + command + "' is not recognized as an operation.");
                    }
                }
            } // EO while

        }
        catch (SocketTimeoutException e)
        {
            log.info("Sesion closed due to inactivity.");
            sendLine(newLine + "Session timed-out due to inactivity.");
        }
        catch (Exception e)
        {
            log.severe(e.getClass().getName() + ": " + e.getMessage());
        }
        finally
        {
            if (socket != null)
            {
                log.info("SMART Console session closed.");
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

    private void startAgent(String agent) throws IOException
    {
        if (agent == null || agent.equals(""))
        {
            sendLine("Missing parameter: <agent-class>");
        }
        else
        {
            sendLine(String.format("Starting %s...", agent));
            try
            {
                manager.startAgent(agent);
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

    private void runAgent(String agent) throws IOException
    {
        if (agent == null || agent.equals(""))
        {
            sendLine("Missing parameter: <agent-class>");
        }
        else
        {
            String message = String.format("Running %s...", agent);
            sendLine(message);
            log.info(message);
            try
            {
                manager.runNow(agent);
                sendLine("Agent task finished. See agent log for details.");
            }
            catch (IllegalStateException e)
            {
                log.warning("Illegal state: " + e.getMessage());
                sendLine(e.getMessage());
            }
            catch (IllegalArgumentException e)
            {
                log.warning(e.getMessage());
                sendLine(e.getMessage());
            }
            catch (UnsupportedOperationException e)
            {
                log.warning("Unsupported operation: " + e.getMessage());
                sendLine("Unsupported operation: " + e.getMessage());
            }
        }
    }

    private void stopAgent(String agent) throws IOException
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

    private void resetAgent(String agent) throws IOException
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

    private void status(String agent) throws IOException
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

    private void showAgents() throws IOException
    {
        Collection<Agent> allAgents = manager.getAgents();
        if (allAgents.size() > 0)
        {
            sendLine("");
            sendLine("NAME                                 TYPE   STATE  ");
            sendLine("------------------------------------ ------ -------");

            for (Agent a : allAgents)
            {
                String type = "";
                if (a instanceof DaemonAgent)
                {
                    type = "DAEMON";
                }
                else if (a instanceof TimerAgent)
                {
                    type = "TIMER";
                }
                sendLine(String.format(showAgentFormat, a.getName(), type, a.getState()));
            }
        }
        else
        {
            sendLine("No agent found.");
        }
    }

    private void showThreads() throws IOException
    {
        sendLine("");
        sendLine("ID   NAME                             STATE        ");
        sendLine("---- -------------------------------- -------------");

        for (ThreadInfo i : SystemUtil.getAllSystemTheadsInfo())
        {
            sendLine(String.format(showThreadFormat, i.getThreadId(), i.getThreadName(), i.getThreadState()));
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
