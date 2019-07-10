package net.obvj.smart.console;

import java.io.PrintWriter;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.DateUtil;
import net.obvj.smart.util.SystemUtil;

public enum Command
{
    SHOW_AGENTS("show-agents")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            Collection<Agent> agents = AgentManager.getInstance().getAgents();
            if (agents.isEmpty())
            {
                out.println("No agent found");
                return;
            }
            out.println("");
            out.println("NAME                                 TYPE   STATE  ");
            out.println("------------------------------------ ------ -------");

            for (Agent agent : agents)
            {
                out.println(String.format("%-36s %-6s %-7s", agent.getName(), agent.getType(), agent.getState()));
            }
        }
    },

    SHOW_THREADS("show-threads")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println("");
            out.println("ID   NAME                             STATE        ");
            out.println("---- -------------------------------- -------------");

            for (ThreadInfo thread : SystemUtil.getAllSystemTheadsInfo())
            {
                out.println(String.format("%-4d %-32s %-13s", thread.getThreadId(), thread.getThreadName(),
                        thread.getThreadState()));
            }
        }
    },
    
    UPTIME("uptime")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println(SystemUtil.getSystemUptime() + " milliseconds");
        }
    },

    START("start")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                log.log(Level.INFO, "Command received: %s", parameters);
                String agent = parameters[1];
                if (agent == null || agent.equals(""))
                {
                    out.println("Missing parameter: <agent-name>");
                }
                else
                {
                    out.println(String.format("Starting %s...", agent));
                    try
                    {
                        AgentManager.getInstance().startAgent(agent);
                        out.println(String.format("%s started", agent));
                    }
                    catch (IllegalStateException e)
                    {
                        String warningMessage = e.getClass().getName() + ": " + e.getMessage();
                        log.warning(warningMessage);
                        out.println(warningMessage);
                    }
                    catch (IllegalArgumentException e)
                    {
                        log.warning(e.getMessage());
                        out.println(e.getMessage());
                    }                    
                }
            }
            else
            {
                out.println("Missing parameter: <agent-name>");
            }
        }
    },

    RUN("run")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                log.log(Level.INFO, "Command received: %s", parameters);
                String agent = parameters[1];
                if (agent == null || agent.equals(""))
                {
                    out.println("Missing parameter: <agent-name>");
                }
                else
                {
                    String message = String.format("Running %s...", agent);
                    out.println(message);
                    log.info(message);
                    try
                    {
                        AgentManager.getInstance().runNow(agent);
                        out.println("Agent task finished. See agent logs for details.");
                    }
                    catch (IllegalStateException | UnsupportedOperationException e)
                    {
                        String warningMessage = e.getClass().getName() + ": " + e.getMessage();
                        log.warning(warningMessage);
                        out.println(warningMessage);
                    }
                    catch (IllegalArgumentException e)
                    {
                        log.warning(e.getMessage());
                        out.println(e.getMessage());
                    }
                    
                }
            }
            else
            {
                out.println("Missing parameter: <agent-name>");
            }
        }
    },

    STOP("stop")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                log.log(Level.INFO, "Command received: %s", parameters);
                String agent = parameters[1];
                if (agent == null || agent.equals(""))
                {
                    out.println("Missing parameter: <agent-name>");
                }
                else
                {
                    String message = String.format("Stopping %s...", agent);
                    out.println(message);
                    log.info(message);
                    try
                    {
                        AgentManager.getInstance().stopAgent(agent);
                        out.println("Success.");
                    }
                    catch (IllegalStateException e)
                    {
                        log.warning("Illegal state: " + e.getMessage());
                        out.println(e.getMessage());
                    }
                    catch (IllegalArgumentException e)
                    {
                        log.warning(e.getMessage());
                        out.println(e.getMessage());
                    }
                    catch (TimeoutException ex)
                    {
                        String errMessage = String.format("Timeout waiting for agent task to complete: %s", agent);
                        out.println(errMessage);
                        log.warning(errMessage);
                    }
                    catch (UnsupportedOperationException e)
                    {
                        String errMessage = String.format("Unsupported operation: %s", e.getMessage());
                        out.println(errMessage);
                        log.warning(errMessage);
                    }
                }
            }
            else
            {
                out.println("Missing parameter: <agent-name>");
            }
        }
    },

    STATUS("status")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                log.log(Level.INFO, "Command received: %s", parameters);
                String agent = parameters[1];
                if (agent == null || agent.equals(""))
                {
                    out.println("Missing parameter: <agent-name>");
                }
                else
                {
                    try
                    {
                        out.println(AgentManager.getInstance().getAgentStatusStr(agent));
                    }
                    catch (IllegalArgumentException e)
                    {
                        log.warning(e.getMessage());
                        out.println(e.getMessage());
                    }
                }
            }
            else
            {
                out.println("Missing parameter: <agent-name>");
            }
        }
    },

    RESET("reset")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                log.log(Level.INFO, "Command received: %s", parameters);
                String agent = parameters[1];
                if (agent == null || agent.equals(""))
                {
                    out.println("Missing parameter: <agent-name>");
                }
                else
                {
                    String message = String.format("Resetting %s...", agent);
                    out.println(message);
                    log.info(message);
                    try
                    {
                        AgentManager.getInstance().resetAgent(agent);
                        out.println("Success.");
                    }
                    catch (IllegalStateException e)
                    {
                        log.warning("Illegal state: " + e.getMessage());
                        out.println(e.getMessage());
                    }
                    catch (IllegalArgumentException e)
                    {
                        log.warning(e.getMessage());
                        out.println(e.getMessage());
                    }
                }
            }
            else
            {
                out.println("Missing parameter: <agent-name>");
            }
        }
    },

    DATE("date")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println(DateUtil.now());
        }
    },

    HELP("help")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println("Available commands:");
            Arrays.stream(values()).forEachOrdered(command -> out.println(" - " + command.getString()));
        }
    },

    EXIT("exit")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            // Do nothing...
        }
    };

    private final static Logger log = Logger.getLogger("smart");

    private final String string;

    private Command(String string)
    {
        this.string = string;
    }

    public String getString()
    {
        return string;
    }

    public abstract void execute(String[] parameters, PrintWriter out);

    public static Command getCommandByString(String string)
    {
        return Arrays.stream(values()).filter(command -> command.getString().equals(string)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + string));
    }
}
