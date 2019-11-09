package net.obvj.smart.console.enhanced.commands;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/**
 * A command that prints Java Runtime/VM version information
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "java",
         headerHeading = "@|bold,underline Usage|@:%n%n",
         header = "Display Java Runtime/VM information",
         synopsisHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n%n",
         description = "Retrieve server-side Java runtime/VM information.",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class JavaVersionCommand implements Runnable
{
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;
    
    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        String javaVersion = AgentManagerJMXClient.getMBeanProxy().getJavaVersion();
        parent.out.println(javaVersion);
    }

    protected void setParent(Commands parent)
    {
        this.parent = parent;
    }

}
