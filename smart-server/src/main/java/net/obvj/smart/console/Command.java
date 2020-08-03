package net.obvj.smart.console;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.obvj.smart.agents.dto.AgentDTO;
import net.obvj.smart.jmx.dto.ThreadDTO;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.ApplicationContextFacade;
import net.obvj.smart.util.DateUtils;
import net.obvj.smart.util.Exceptions;
import net.obvj.smart.util.SystemUtils;

/**
 * A set of commands and business logic available to the classic management console
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public enum Command
{
    SHOW_AGENTS("agents", "ls")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            Collection<AgentDTO> agents = ApplicationContextFacade.getBean(AgentManager.class).getAgentDTOs();

            if (!(parameters.length > 1 && StringUtils.equalsAny(parameters[1], "-a", "--all")))
            {
                agents = agents.stream().filter(agent -> !agent.isHidden()).collect(Collectors.toList());
            }

            if (agents.isEmpty())
            {
                out.println("No agent found");
                return;
            }
            out.print(agents.size());
            out.println(" agent(s) found");
            out.println("");
            out.println("Name                                       Type   State");
            out.println("------------------------------------------ ------ -------");

            for (AgentDTO agent : agents)
            {
                out.println(String.format("%-42s %-6s %-7s", agent.getName(), agent.getType(), agent.getState()));
            }
        }
    },

    SHOW_THREADS("threads")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            Collection<ThreadDTO> threads = SystemUtils.getAllSystemTheadsDTOs();

            if (parameters.length > 1 && StringUtils.isNotEmpty(parameters[1]))
            {
                String searchRegex = parameters[1].toLowerCase().replaceAll("\\*", ".*");
                Predicate<? super ThreadDTO> predicate = thread -> thread.getName().toLowerCase().matches(searchRegex);
                threads = threads.stream().filter(predicate).collect(Collectors.toSet());
            }
            if (threads.isEmpty())
            {
                out.println("No thread found");
                return;
            }

            out.print(threads.size());
            out.println(" thread(s) found");
            out.println("");
            out.println("ID   Name                                   State");
            out.println("---- -------------------------------------- -------------");

            for (ThreadDTO thread : threads)
            {
                out.println(String.format("%-4d %-38s %-13s", thread.getId(), thread.getName(), thread.getState()));
            }
        }
    },

    UPTIME("uptime")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println(SystemUtils.getSystemUptime() + " milliseconds");
        }
    },

    START("start")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            if (parameters.length == 2)
            {
                String agent = parameters[1];
                out.println(String.format("Starting %s...", agent));
                try
                {
                    ApplicationContextFacade.getBean(AgentManager.class).startAgent(agent);
                    out.println(String.format("%s started", agent));
                }
                catch (IllegalArgumentException | IllegalStateException e)
                {
                    String warningMessage = e.getClass().getName() + ": " + e.getMessage();
                    LOG.warn(warningMessage);
                    out.println(warningMessage);
                }
            }
            else
            {
                out.println(MISSING_PARAMETER_AGENT_NAME);
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
                String agent = parameters[1];
                String message = String.format("Running %s...", agent);
                out.println(message);
                LOG.info(message);
                try
                {
                    ApplicationContextFacade.getBean(AgentManager.class).runNow(agent);
                    out.println("Agent task finished. See agent logs for details.");
                }
                catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException e)
                {
                    String warningMessage = e.getClass().getName() + ": " + e.getMessage();
                    LOG.warn(warningMessage);
                    out.println(warningMessage);
                }
            }
            else
            {
                out.println(MISSING_PARAMETER_AGENT_NAME);
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
                String agent = parameters[1];
                String message = String.format("Stopping %s...", agent);
                out.println(message);
                LOG.info(message);
                try
                {
                    ApplicationContextFacade.getBean(AgentManager.class).stopAgent(agent);
                    out.println("Success.");
                }
                catch (IllegalArgumentException | IllegalStateException | TimeoutException e)
                {
                    LOG.warn(e.getMessage());
                    out.println(e.getMessage());
                }
            }
            else
            {
                out.println(MISSING_PARAMETER_AGENT_NAME);
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
                String agent = parameters[1];
                try
                {
                    out.println(ApplicationContextFacade.getBean(AgentManager.class).getAgentStatusStr(agent));
                }
                catch (IllegalArgumentException e)
                {
                    LOG.warn(e.getMessage());
                    out.println(e.getMessage());
                }
            }
            else
            {
                out.println(MISSING_PARAMETER_AGENT_NAME);
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
                String agent = parameters[1];
                String message = String.format("Resetting %s...", agent);
                out.println(message);
                LOG.info(message);
                try
                {
                    ApplicationContextFacade.getBean(AgentManager.class).resetAgent(agent);
                    out.println("Success.");
                }
                catch (IllegalArgumentException | IllegalStateException | ReflectiveOperationException e)
                {
                    LOG.warn(e.getMessage());
                    out.println(e.getMessage());
                }
            }
            else
            {
                out.println(MISSING_PARAMETER_AGENT_NAME);
            }
        }
    },

    DATE("date")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println(DateUtils.formattedCurrentDate());
        }
    },

    JAVA_VERSION("java")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println(SystemUtils.getJavaVersion());
        }
    },

    HELP("help")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            out.println("Available commands:");
            Arrays.stream(values())
                    .sorted((command1, command2) -> StringUtils.compare(command1.getName(), command2.getName()))
                    .forEach(command -> printCommand(out, command));
        }

        private void printCommand(PrintWriter out, Command command)
        {
            out.println(" - " + command.name + (command.hasAlias() ? ", " + command.alias : ""));
        }
    };

    protected static final String MISSING_PARAMETER_AGENT_NAME = "Missing parameter: <agent-name>";
    private static final Logger LOG = LoggerFactory.getLogger(Command.class);

    private final String name;
    private final String alias;

    private Command(String name)
    {
        this(name, "");
    }

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
        return StringUtils.isNotEmpty(alias);
    }

    public boolean isIdentifiableBy(final String string)
    {
        return name.equals(string) || (hasAlias() && alias.equals(string));
    }

    public String getName()
    {
        return name;
    }

    public abstract void execute(String[] parameters, PrintWriter out);

    public static Command getByNameOrAlias(String string)
    {
        return getOptionalByNameOrAlias(string)
                .orElseThrow(() -> Exceptions.illegalArgument("Unknown command: %s", string));
    }

    public static Command getByNameOrAliasOrNull(String string)
    {
        return getOptionalByNameOrAlias(string).orElse(null);
    }

    public static Optional<Command> getOptionalByNameOrAlias(String string)
    {
        Command[] commands = values();
        return Arrays.stream(commands).filter(command -> command.isIdentifiableBy(string)).findFirst();
    }

}
