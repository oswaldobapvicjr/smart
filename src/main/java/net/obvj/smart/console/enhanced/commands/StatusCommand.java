package net.obvj.smart.console.enhanced.commands;

import java.io.IOException;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * A command that prints detailed status of a given agent
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "status",
         headerHeading = "@|bold,underline Usage|@:%n%n",
         header = "Show agent status",
         synopsisHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n%n",
         description = "Display current information and status about the agent identified with the given name.",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class StatusCommand implements Runnable
{
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;
    
    @Parameters(paramLabel = "<agent>", description = "The agent whose status will be retrieved.")
    private String agent;
    
    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        try
        {
            parent.out.println(AgentManagerJMXClient.getMBeanProxy().getAgentStatusStr(agent));
        }
        catch (IllegalStateException | IllegalArgumentException e)
        {
            parent.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            parent.out.println("Unable to connect to the agent manager. Please make sure the service is running.");
        }
    }

    protected void setAgent(String agent)
    {
        this.agent = agent;
    }

    protected void setParent(Commands parent)
    {
        this.parent = parent;
    }
}
