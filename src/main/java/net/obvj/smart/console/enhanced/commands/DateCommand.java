package net.obvj.smart.console.enhanced.commands;

import java.io.IOException;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/**
 * A command that prints server date and time
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "date",
         headerHeading = "@|bold,underline Usage|@:%n%n",
         header = "Display server date",
         synopsisHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n%n",
         description = "Retrieve server current date and time",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class DateCommand implements Runnable
{
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;
    
    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        try
        {
            parent.out.println(AgentManagerJMXClient.getMBeanProxy().getServerDate());
        }
        catch (IOException e)
        {
            parent.out.println("Unable to connect to the agent manager. Please make sure the service is running.");
        }
    }
    
    protected void setParent(Commands parent)
    {
        this.parent = parent;
    }
}
