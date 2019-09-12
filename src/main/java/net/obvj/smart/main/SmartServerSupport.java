package net.obvj.smart.main;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.conf.SmartProperties;
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
    public static final Logger LOG = Logger.getLogger("smart-server");

    protected static boolean runFlag = true;

    public boolean isClassicConsoleEnabled()
    {
        return SmartProperties.getInstance().getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED);
    }

    protected void startClassicManagementConsole()
    {
        if (isClassicConsoleEnabled())
        {
            LOG.info("Starting classic Agent Management Console...");
            ManagementConsole.getInstance().start();
        }
        else
        {
            LOG.fine("Classic Agent Management Console not enabled");
        }
    }

    protected void closeClassicManagementConsole()
    {
        if (isClassicConsoleEnabled())
        {
            LOG.info("Closing Agent Management Console...");
            ManagementConsole.getInstance().stop();
        }
    }

    protected void startAutomaticAgents()
    {
        AgentManager manager = AgentManager.getInstance();
        LOG.log(Level.INFO, "Starting agents...");
        manager.getAgents().stream().filter(agent -> agent.getConfiguration().isAutomaticallyStarted())
                .forEach(agent -> manager.startAgent(agent.getName()));
    }
}
