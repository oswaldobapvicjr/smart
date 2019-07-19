package net.obvj.smart.console.enhanced.commands;

import java.io.IOException;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

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
    
    @Parameters(paramLabel = "<agent>", description = "The agent to be run.")
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
        catch (IOException e)
        {
            parent.out.println("Unable to connect to the agent manager. Please make sure the service is running.");
        }
    }
}
