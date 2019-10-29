package net.obvj.smart.main;

import java.util.List;
import java.util.logging.Level;

import javax.management.JMException;

import net.obvj.smart.conf.AgentConfigurationException;
import net.obvj.smart.conf.AgentsXml;
import net.obvj.smart.conf.xml.AgentConfiguration;

/**
 * S.M.A.R.T. service main class
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class Main extends SmartServerSupport implements Runnable
{
    @Override
    public void run()
    {
        try
        {
            LOG.info("Starting S.M.A.R.T. Agent Manager...");
            startClassicManagementConsole();
            registerManagedBean();

            // Create shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(), "Shutdown"));

            // Loading agents
            LOG.info("Loading agents configuration...");
            List<AgentConfiguration> xmlAgents = AgentsXml.getInstance().getAgents();

            loadAgents(xmlAgents);
            startAutomaticAgents();

            LOG.info("Ready.");

        }
        catch (JMException e)
        {
            LOG.log(Level.SEVERE, "Unable to register S.M.A.R.T. management bean", e);
            System.exit(1);
        }
        catch (AgentConfigurationException e)
        {
            LOG.log(Level.SEVERE, "Unable to load agents configuration", e);
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
        new Main().run();
    }

}
