package net.obvj.smart.console.enhanced.commands;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * A command that stops a given agent
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "stop",
         headerHeading = "@|bold,underline Usage|@:%n%n",
         header = "Stops an agent",
         synopsisHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n%n",
         description = "Gracefully stop the agent identified with the given name.",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class StopCommand implements Runnable
{
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;
    
    @Parameters(paramLabel = "<agent>", description = "The agent to be stopped.")
    private String agent;
    
    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        try
        {
            parent.out.printf("Stopping %s...%n", agent);
            parent.out.flush();
            AgentManagerJMXClient.getMBeanProxy().stopAgent(agent);
            parent.out.println("Success");
        }
        catch (IllegalStateException | IllegalArgumentException e)
        {
            parent.out.println(e.getMessage());
        }
        catch (TimeoutException e)
        {
            parent.out.println("Operation timed-out. The agent is still running. Try again later.");
        }
        catch (IOException e)
        {
            parent.out.println("Unable to connect to the agent manager. Please make sure the service is running.");
        }
    }
}
