package net.obvj.smart.console;

import java.io.PrintWriter;
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
        public void execute(String[] parameters, PrintWriter out)
        {
            Collection<Agent> agents = AgentManager.getInstance().getAgents();
            if (agents.isEmpty())
            {
                out.println("No agent found");
                return;
            }
            out.println(CommandWorker.LINE_SEPARATOR);
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
            out.println(CommandWorker.LINE_SEPARATOR);
            out.println("ID   NAME                             STATE        ");
            out.println("---- -------------------------------- -------------");

            for (ThreadInfo thread : SystemUtil.getAllSystemTheadsInfo())
            {
                out.println(String.format("%-4d %-32s %-13s", thread.getThreadId(), thread.getThreadName(),
                        thread.getThreadState()));
            }
        }
    },

    START("start")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            // TODO Auto-generated method stub
        }
    },

    RUN("run")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            // TODO Auto-generated method stub
        }
    },

    STOP("stop")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            // TODO Auto-generated method stub
        }
    },

    STATUS("status")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            // TODO Auto-generated method stub
        }
    },

    RESET("reset")
    {
        @Override
        public void execute(String[] parameters, PrintWriter out)
        {
            // TODO Auto-generated method stub
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
            Arrays.stream(values()).forEach(command -> out.println(" - " + command.getString()));
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

    private String string;

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
