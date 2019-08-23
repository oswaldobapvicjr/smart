package net.obvj.smart.console.enhanced.commands;

import java.io.IOException;
import java.util.Collection;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.jmx.dto.ThreadDTO;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/**
 * A command that prints currently active threads
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "threads",
         headerHeading = "@|bold,underline Usage|@:%n%n",
         header = "List server threads",
         synopsisHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n%n",
         description = "Display all currently active threads and their states in the server.",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class ThreadsCommand implements Runnable
{
    private static final String ID_NAME_STATE_PATTERN = "%-4d %-38s %-13s%n";

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;

    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        parent.out.println("Listing active server threads...");
        parent.out.flush();
        try
        {
            Collection<ThreadDTO> allThreads = AgentManagerJMXClient.getMBeanProxy().getAllThreadsInfo();

            parent.out.println();
            parent.out.println("ID   Name                                   State");
            parent.out.println("---- -------------------------------------- -------------");
            allThreads.forEach(thread -> parent.out.printf(ID_NAME_STATE_PATTERN, thread.id, thread.name, thread.state));
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
