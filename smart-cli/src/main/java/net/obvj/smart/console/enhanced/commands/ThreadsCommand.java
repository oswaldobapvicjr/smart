package net.obvj.smart.console.enhanced.commands;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.jmx.dto.ThreadDTO;
import net.obvj.smart.util.ClientApplicationContextFacade;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * A command that prints currently active threads
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "threads",
         headerHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n",
         description = "Lists all active threads in the server side and their states.",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class ThreadsCommand implements Runnable
{
    private static final String ID_NAME_STATE_PATTERN = "%-4d %-38s %-13s%n";

    private AgentManagerJMXClient client = ClientApplicationContextFacade.getBean(AgentManagerJMXClient.class);

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;

    @Parameters(paramLabel = "[<name>]", description = "The name(s) to filter (supports wildcard character \"*\").", defaultValue = "")
    private String name = "";

    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        parent.out.println("Listing active server threads...");
        parent.out.flush();

        AgentManagerJMXMBean mBeanProxy = client.getMBeanProxy();
        Collection<ThreadDTO> threads = mBeanProxy.getAllThreadsInfo();

        if (!name.isEmpty())
        {
            String searchRegex = name.toLowerCase().replaceAll("\\*", ".*");
            Predicate<? super ThreadDTO> predicate = thread -> thread.getName().toLowerCase().matches(searchRegex);
            threads = threads.stream().filter(predicate).collect(Collectors.toSet());
        }
        if (threads.isEmpty())
        {
            parent.out.println("No thread found");
            return;
        }

        printThreads(threads);
    }

    private void printThreads(Collection<ThreadDTO> threads)
    {
        parent.out.print(threads.size());
        parent.out.println(" thread(s) found");

        parent.out.println();
        parent.out.println("ID   Name                                   State");
        parent.out.println("---- -------------------------------------- -------------");
        threads.forEach(this::printThread);
    }

    private void printThread(ThreadDTO thread)
    {
        parent.out.printf(ID_NAME_STATE_PATTERN, thread.getId(), thread.getName(), thread.getState());
    }

    protected void setName(String name)
    {
        this.name = name;
    }

}
