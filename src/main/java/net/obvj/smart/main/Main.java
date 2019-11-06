package net.obvj.smart.main;

import java.util.List;

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
public class Main extends SmartServerSupport
{
    /**
     * S.M.A.R.T. Server startup sequence.
     * 
     * @throws JMException                 if unable to register S.M.A.R.T. management beans
     * @throws AgentConfigurationException if unable to load agents configuration file
     */
    public void start() throws JMException
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

    public static void main(String[] args) throws JMException
    {
        new Main().start();
    }

}
