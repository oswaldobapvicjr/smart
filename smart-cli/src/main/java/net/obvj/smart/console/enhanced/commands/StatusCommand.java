package net.obvj.smart.console.enhanced.commands;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.util.ClientApplicationContextFacade;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

/**
 * A command that prints detailed status of a given agent
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "status",
         headerHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n",
         description = "Displays current status of a given agent.",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class StatusCommand implements Runnable
{
    private AgentManagerJMXClient client = ClientApplicationContextFacade.getBean(AgentManagerJMXClient.class);

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;

    @Parameters(paramLabel = "<agent>", description = "The agent whose status will be retrieved.", completionCandidates = AgentCompletionCandidates.class)
    private String agent;

    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        try
        {
            AgentManagerJMXMBean mBeanProxy = client.getMBeanProxy();
            parent.out.println(mBeanProxy.getAgentStatusStr(agent));
        }
        catch (IllegalStateException | IllegalArgumentException e)
        {
            parent.out.println(e.getMessage());
        }
    }

    protected void setAgent(String agent)
    {
        this.agent = agent;
    }

}
