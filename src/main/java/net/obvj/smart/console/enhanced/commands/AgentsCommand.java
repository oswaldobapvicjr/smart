package net.obvj.smart.console.enhanced.commands;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.util.ClientApplicationContextFacade;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * A command that lists agents
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
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
    private static final String NAME_TYPE_STATE_PATTERN = "%-42s %-6s %-7s%n";

    private AgentManagerJMXClient client = ClientApplicationContextFacade.getBean(AgentManagerJMXClient.class);

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;
    
    @Option(names = { "-a", "--all" }, description = "List all agents (including hidden ones).")
    private boolean all = false;

    @Option(names = { "-t", "--type" }, description = "List agents of a specific type.")
    private String type = "";
    
    @Parameters(paramLabel = "[<name>]", description = "The name(s) to filter (supports wildcard character \"*\").", defaultValue = "")
    private String name = "";

    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        parent.out.println("Listing agents...");
        parent.out.flush();

        Collection<AgentDTO> agents = client.getMBeanProxy().getAgentDTOs();
        if (!all)
        {
            agents = agents.stream().filter(a -> !a.isHidden()).collect(Collectors.toSet());
        }
        if (!type.isEmpty())
        {
            agents = agents.stream().filter(a -> a.getType().equalsIgnoreCase(this.type)).collect(Collectors.toSet());
        }
        if (!name.isEmpty())
        {
            String searchRegex = name.toLowerCase().replaceAll("\\*", ".*");
            Predicate<? super AgentDTO> predicate = agent -> agent.getName().toLowerCase().matches(searchRegex);
            agents = agents.stream().filter(predicate).collect(Collectors.toSet());
        }
        if (agents.isEmpty())
        {
            parent.out.println("No agent found");
            return;
        }
        parent.out.println();
        parent.out.println("Name                                       Type   State");
        parent.out.println("------------------------------------------ ------ -------");
        agents.forEach(this::printAgent);
    }

    private void printAgent(AgentDTO agent)
    {
        parent.out.printf(String.format(NAME_TYPE_STATE_PATTERN, agent.getName(), agent.getType(), agent.getState()));
    }

    protected void setType(String type)
    {
        this.type = type;
    }

    protected void setAll(boolean all)
    {
        this.all = all;
    }
    
    protected void setName(String name)
    {
        this.name = name;
    }

}
