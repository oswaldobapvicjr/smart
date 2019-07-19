package net.obvj.smart.console.enhanced.commands;

import java.io.IOException;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

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
    
    @Parameters(paramLabel = "<agent>", description = "The agent to be reset.")
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
        catch (IllegalStateException | IllegalArgumentException e)
        {
            parent.out.println(e.getMessage());
        }
        catch (IOException e)
        {
            parent.out.println("Unable to connect to the agent manager. Please make sure the service is running.");
        }
    }
}
