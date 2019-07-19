package net.obvj.smart.console.enhanced.commands;

import java.util.Collection;
import java.util.stream.Collectors;

import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(name = "agents",
         aliases = "ls",
         headerHeading = "@|bold,underline Usage|@:%n%n",
         header = "List agents",
         synopsisHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n%n",
         description = "Shows all managed agents and their current states",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class AgentsCommand implements Runnable
{
    private static final String NAME_TYPE_STATE_PATTERN = "%-39s %-6s %-7s%n";

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;
    
    @Option(names = { "-t", "--type" }, description = "List agents of a specific type.")
    private String type = "";
    
    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        Collection<AgentDTO> agents = AgentManagerJMXClient.getInstance().getMBeanProxy().getAgentsDTO();
        
        if (!type.isEmpty())
        {
            agents = agents.stream().filter(a -> a.type.equalsIgnoreCase(this.type)).collect(Collectors.toSet());
        }
        if (agents.isEmpty())
        {
            parent.out.println("No agent found");
            return;
        }
        parent.out.println();
        parent.out.println("Name                                    Type   State");
        parent.out.println("--------------------------------------- ------ -------");

        agents.forEach(agent -> parent.out
                .printf(String.format(NAME_TYPE_STATE_PATTERN, agent.name, agent.type, agent.state)));
    }
}
