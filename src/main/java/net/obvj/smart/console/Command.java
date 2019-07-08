package net.obvj.smart.console;

import java.lang.management.ThreadInfo;
import java.util.Arrays;
import java.util.Collection;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.DateUtil;
import net.obvj.smart.util.SystemUtil;

public enum Command
{
    SHOW_AGENTS("show-agents")
    {
        @Override
        public CommandOutput execute(String... parameters)
        {
            Collection<Agent> agents = AgentManager.getInstance().getAgents();
            if (agents.isEmpty())
            {
                return new CommandOutput("No agent found", true);
            }
            StringBuilder output = new StringBuilder();
            output.append(CommandWorker.LINE_SEPARATOR);
            output.append("NAME                                 TYPE   STATE  ").append(CommandWorker.LINE_SEPARATOR);
            output.append("------------------------------------ ------ -------").append(CommandWorker.LINE_SEPARATOR);

            for (Agent agent : agents)
            {
                output.append(String.format("%-36s %-6s %-7s%n", agent.getName(), agent.getType(), agent.getState()));
            }
            return new CommandOutput(output.toString(), false);
        }
    },

    SHOW_THREADS("show-threads")
    {
        @Override
        public CommandOutput execute(String... parameters)
        {
            StringBuilder output = new StringBuilder();
            output.append(CommandWorker.LINE_SEPARATOR);
            output.append("ID   NAME                             STATE        ").append(CommandWorker.LINE_SEPARATOR);
            output.append("---- -------------------------------- -------------").append(CommandWorker.LINE_SEPARATOR);

            for (ThreadInfo thread : SystemUtil.getAllSystemTheadsInfo())
            {
                output.append(String.format("%-4d %-32s %-13s%n", thread.getThreadId(), thread.getThreadName(),
                        thread.getThreadState()));
            }
            return new CommandOutput(output.toString(), false);
        }
    },

    START("start")
    {
        @Override
        public CommandOutput execute(String... parameters)
        {
            // TODO Auto-generated method stub
            return null;
        }
    },

    RUN("run")
    {
        @Override
        public CommandOutput execute(String... parameters)
        {
            // TODO Auto-generated method stub
            return null;
        }
    },

    STOP("stop")
    {
        @Override
        public CommandOutput execute(String... parameters)
        {
            // TODO Auto-generated method stub
            return null;
        }
    },

    STATUS("status")
    {
        @Override
        public CommandOutput execute(String... parameters)
        {
            // TODO Auto-generated method stub
            return null;
        }
    },

    RESET("reset")
    {
        @Override
        public CommandOutput execute(String... parameters)
        {
            // TODO Auto-generated method stub
            return null;
        }
    },

    DATE("date")
    {
        @Override
        public CommandOutput execute(String... parameters)
        {
            return new CommandOutput(DateUtil.now(), false);
        }
    },

    HELP("help")
    {
        @Override
        public CommandOutput execute(String... parameters)
        {
            StringBuilder output = new StringBuilder();
            output.append("Available commands:").append(CommandWorker.LINE_SEPARATOR);
            Arrays.stream(values()).forEach(
                    command -> output.append(" - ").append(command.getString()).append(CommandWorker.LINE_SEPARATOR));
            return new CommandOutput(output.toString(), false);
        }
    },

    EXIT("exit")
    {
        @Override
        public CommandOutput execute(String... parameters)
        {
            return new CommandOutput("", false);
        }
    };

    private String string;

    private Command(String string)
    {
        this.string = string;
    }

    public String getString()
    {
        return string;
    }

    public abstract CommandOutput execute(String... parameters);

    public static Command getCommandByString(String string)
    {
        return Arrays.stream(values()).filter(command -> command.getString().equals(string)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown command: " + string));
    }
}
