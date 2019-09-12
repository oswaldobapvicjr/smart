package net.obvj.smart.main;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.conf.AgentsXml;
import net.obvj.smart.conf.AgentConfigurationException;
import net.obvj.smart.conf.xml.AgentConfiguration;
import net.obvj.smart.jmx.AgentManagerJMX;
import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.manager.AgentManager;

/**
 * S.M.A.R.T. service main class
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class Main extends SmartServerSupport implements Runnable
{
    public static final Logger LOG = Logger.getLogger("smart-server");

    private static boolean runFlag = true;

    @Override
    public void run()
    {
        /*
         * STEP 1: Start Agent Manager
         */
        LOG.info("Starting Agent Manager...");
        AgentManager manager = AgentManager.getInstance();

        /*
         * STEP 2: Start classic management console
         */
        startClassicManagementConsole();

        /*
         * STEP 3: Register Managed Beans
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
         * STEP 4: Create shutdown hook
         */
        LOG.info("Creating shutdown hook...");
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(), "Shutdown"));

        /*
         * Step 5: Loading agents configuration
         */
        try
        {
            LOG.info("Loading agents configuration...");
            List<AgentConfiguration> xmlAgents = AgentsXml.getInstance().getAgents();

            /*
             * Step 6: Loading and starting agents objects
             */
            LOG.info("Loading agents objects...");
            xmlAgents.forEach(xmlAgent ->
            {
                try
                {
                    manager.addAgent(Agent.parseAgent(xmlAgent));
                }
                catch (Exception e)
                {
                    LOG.log(Level.SEVERE, "Unable to load agent: " + xmlAgent.getName(), e);
                }
            });

            LOG.log(Level.INFO, "{0} agents loaded", manager.getAgents().size());

            if (manager.getAgents().isEmpty())
            {
                System.exit(1);
            }

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
