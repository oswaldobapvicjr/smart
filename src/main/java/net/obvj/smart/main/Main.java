package net.obvj.smart.main;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.logging.Level;

import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;

import net.obvj.smart.conf.AgentConfigurationException;
import net.obvj.smart.conf.AgentsXml;
import net.obvj.smart.conf.xml.AgentConfiguration;
import net.obvj.smart.jmx.AgentManagerJMX;
import net.obvj.smart.jmx.AgentManagerJMXMBean;

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
        LOG.info("Starting S.M.A.R.T. Agent Manager...");

        /*
         * Start classic management console
         */
        startClassicManagementConsole();

        /*
         * Register Managed Beans
         */
        LOG.info("Creating and registering Managed Beans...");
        try
        {
            AgentManagerJMXMBean mBean = new AgentManagerJMX();
            ObjectName name = new ObjectName("net.obvj.smart.jmx:type=AgentManagerJMX");
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            mBeanServer.registerMBean(mBean, name);
        }
        catch (OperationsException | MBeanRegistrationException exception)
        {
            LOG.log(Level.SEVERE, "Unable to register JMX bean", exception);
        }

        /*
         * Create shutdown hook
         */
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(), "Shutdown"));

        /*
         * Loading agents configuration
         */
        try
        {
            LOG.info("Loading agents configuration...");
            List<AgentConfiguration> xmlAgents = AgentsXml.getInstance().getAgents();

            /*
             * Loading and starting agents
             */
            loadAgents(xmlAgents);
            startAutomaticAgents();

            LOG.info("Ready.");

            keepMainThreadBusy();
        }
        catch (AgentConfigurationException e)
        {
            LOG.log(Level.SEVERE, "Unable to load agents configuration", e);
            System.exit(1);
        }
    }

   private void keepMainThreadBusy()
    {
        while (runFlag)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args)
    {
        new Main().run();
    }

}
