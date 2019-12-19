package net.obvj.smart.main;

import javax.management.JMException;

import net.obvj.smart.conf.AgentConfigurationException;

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
        startClassicManagementConsole();
        registerManagedBean();

        // Create shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(), "Shutdown"));

        // Start agents
        startAutomaticAgents();

        LOG.info("Ready");
    }

    public static void main(String[] args) throws JMException
    {
        new Main().start();
    }

}
