package net.obvj.smart.console.enhanced.commands;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * A command that runs a given agent immediately
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "run",
         headerHeading = "@|bold,underline Usage|@:%n%n",
         header = "Run an agent",
         synopsisHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n%n",
         description = "Execute the runnable task for a given agent immediately.",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class RunCommand implements Runnable
{
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;
    
    @Parameters(paramLabel = "<agent>", description = "The agent to be run.", completionCandidates = AgentCompletionCandidates.class)
    private String agent;
    
    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        try
        {
            parent.out.printf("Running %s...%n", agent);
            parent.out.flush();
            AgentManagerJMXClient.getMBeanProxy().runNow(agent);
            parent.out.println("Success");
        }
        catch (IllegalStateException | IllegalArgumentException | UnsupportedOperationException e)
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
