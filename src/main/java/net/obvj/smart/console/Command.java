package net.obvj.smart.console;

import java.io.PrintWriter;
import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.DateUtil;
import net.obvj.smart.util.SystemUtil;

/**
 * A set of commands and business logic available to the classic management console
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public enum Command
{
    SHOW_AGENTS("show-agents", "agents")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            Collection<AgentDTO> agents = AgentManager.getInstance().getAgentDTOs();
            if (agents.isEmpty())
            {
                out.println("No agent found");
                return;
            }
            out.println("");
            out.println("Name                                       Type   State");
            out.println("------------------------------------------ ------ -------");

            for (AgentDTO agent : agents)
            {
                out.println(String.format("%-42s %-6s %-7s", agent.name, agent.type, agent.state));
            }
        }
    },

    SHOW_THREADS("show-threads", "threads")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println("");
            out.println("ID   Name                                   State");
            out.println("---- -------------------------------------- -------------");

            for (ThreadInfo thread : SystemUtil.getAllSystemTheadsInfo())
            {
                out.println(String.format("%-4d %-38s %-13s", thread.getThreadId(), thread.getThreadName(),
                        thread.getThreadState()));
            }
        }
    },

    UPTIME("uptime", "")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println(SystemUtil.getSystemUptime() + " milliseconds");
        }
    },

    START("start", "")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                String agent = parameters[1];
                if (agent == null || agent.equals(""))
                {
                    out.println(MISSING_PARAMETER_AGENT_NAME);
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
                out.println(MISSING_PARAMETER_AGENT_NAME);
            }
        }
    },

    RUN("run", "")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                String agent = parameters[1];
                if (agent == null || agent.equals(""))
                {
                    out.println(MISSING_PARAMETER_AGENT_NAME);
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
                out.println(MISSING_PARAMETER_AGENT_NAME);
            }
        }
    },

    STOP("stop", "")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                String agent = parameters[1];
                if (agent == null || agent.equals(""))
                {
                    out.println(MISSING_PARAMETER_AGENT_NAME);
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
                out.println(MISSING_PARAMETER_AGENT_NAME);
            }
        }
    },

    STATUS("status", "")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                String agent = parameters[1];
                if (agent == null || agent.equals(""))
                {
                    out.println(MISSING_PARAMETER_AGENT_NAME);
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
                out.println(MISSING_PARAMETER_AGENT_NAME);
            }
        }
    },

    RESET("reset", "")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                String agent = parameters[1];
                if (agent == null || agent.equals(""))
                {
                    out.println(MISSING_PARAMETER_AGENT_NAME);
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
                out.println(MISSING_PARAMETER_AGENT_NAME);
            }
        }
    },

    DATE("date", "")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println(DateUtil.now());
        }
    },

    HELP("help", "")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println("Available commands:");
            Arrays.stream(values()).forEachOrdered(
                    command -> out.println(" - " + command.name + (command.hasAlias() ? ", " + command.alias : "")));
        }
    },

    EXIT("exit", "quit")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            // Do nothing...
        }
    };

    private static final String MISSING_PARAMETER_AGENT_NAME = "Missing parameter: <agent-name>";
    private static final Logger log = Logger.getLogger("smart-server");

    private final String name;
    private final String alias;

    private Command(String name, String alias)
    {
        this.name = name;
        this.alias = alias;
    }

    public String getAlias()
    {
        return alias;
    }
    
    public boolean hasAlias()
    {
        return alias != null && alias != "";
    }
    
    public String getName()
    {
        return name;
    }

    public abstract void execute(String[] parameters, PrintWriter out);

    public static Command getByNameOrAlias(String string)
    {
        return getOptionalByNameOrAlias(string)
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + string));
    }
    
    public static Command getByNameOrAliasOrNull(String string)
    {
        return getOptionalByNameOrAlias(string).orElse(null);
    }

    public static Optional<Command> getOptionalByNameOrAlias(String string)
    {
        return Arrays.stream(values()).filter(command -> command.name.equals(string) || command.alias.equals(string))
                .findFirst();
    }

}
