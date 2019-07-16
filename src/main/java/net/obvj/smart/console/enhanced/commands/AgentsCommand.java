package net.obvj.smart.console.enhanced.commands;

import java.util.Collection;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Command(name = "agents", aliases = "ls", mixinStandardHelpOptions = true, description = "lists managed agents and their current statuses", version = "1.0")
public class AgentsCommand implements Runnable
{
    @ParentCommand
    Commands parent;

    @Override
    public void run()
    {
        Collection<String> agents = AgentManagerJMXClient.getInstance().getMBeanProxy().getAgents();
        agents.forEach(parent.out::println);
    }
}
