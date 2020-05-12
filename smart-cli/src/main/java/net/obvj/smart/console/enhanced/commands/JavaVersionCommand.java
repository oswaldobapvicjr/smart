package net.obvj.smart.console.enhanced.commands;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.util.ClientApplicationContextFacade;
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
         headerHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n",
         description = "Retrieves server-side Java runtime/VM information.",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class JavaVersionCommand implements Runnable
{
    private AgentManagerJMXClient client = ClientApplicationContextFacade.getBean(AgentManagerJMXClient.class);

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;

    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        AgentManagerJMXMBean mBeanProxy = client.getMBeanProxy();
        parent.out.println(mBeanProxy.getJavaVersion());
    }

}
