package net.obvj.smart.main;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.conf.SmartProperties;
import net.obvj.smart.conf.xml.AgentConfiguration;
import net.obvj.smart.console.ManagementConsole;
import net.obvj.smart.manager.AgentManager;

/**
 * S.M.A.R.T. server support methods
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class SmartServerSupport
{
    protected static final Logger LOG = Logger.getLogger("smart-server");

    protected static boolean runFlag = true;

    public boolean isClassicConsoleEnabled()
    {
        return SmartProperties.getInstance().getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED);
    }

    protected void startClassicManagementConsole()
    {
        if (isClassicConsoleEnabled())
        {
            LOG.info("Starting Classic Management Console...");
            ManagementConsole.getInstance().start();
        }
        else
        {
            LOG.fine("Classic Management Console not enabled");
        }
    }

    protected void closeClassicManagementConsole()
    {
        if (isClassicConsoleEnabled())
        {
            LOG.info("Closing Classic Management Console...");
            ManagementConsole.getInstance().stop();
            LOG.info("Classic Management Console closed");
        }
    }

    protected void loadAgents(List<AgentConfiguration> agents)
    {
        LOG.info("Loading agents...");
        AgentManager manager = AgentManager.getInstance();
        agents.forEach(agentConfig ->
        {
            try
            {
                manager.addAgent(Agent.parseAgent(agentConfig));
            }
            catch (Exception e)
            {
                LOG.log(Level.SEVERE, "Unable to load agent: " + agentConfig.getName(), e);
            }
        });
        LOG.log(Level.INFO, "{0} agents loaded", manager.getAgents().size());
    }

    protected void startAutomaticAgents()
    {
        LOG.log(Level.INFO, "Starting agents...");
        AgentManager manager = AgentManager.getInstance();
        manager.getAgents().stream().filter(agent -> agent.getConfiguration().isAutomaticallyStarted())
                .forEach(agent -> manager.startAgent(agent.getName()));
    }

}
