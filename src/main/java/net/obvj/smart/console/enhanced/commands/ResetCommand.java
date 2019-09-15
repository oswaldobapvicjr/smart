package net.obvj.smart.console.enhanced.commands;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * A command that resets a given agent
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "reset",
         headerHeading = "@|bold,underline Usage|@:%n%n",
         header = "Resets an agent",
         synopsisHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n%n",
         description = "Reset the agent identified with the given name.",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class ResetCommand implements Runnable
{
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;
    
    @Parameters(paramLabel = "<agent>", description = "The agent to be reset", completionCandidates = AgentCompletionCandidates.class)
    private String agent;
    
    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        try
        {
            AgentManagerJMXClient.getMBeanProxy().resetAgent(agent);
            parent.out.println("Success");
        }
        catch (IllegalStateException | IllegalArgumentException | ReflectiveOperationException e)
        {
            parent.out.println(e.getMessage());
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
