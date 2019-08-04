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
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.conf.SmartProperties;
import net.obvj.smart.conf.xml.XmlAgent;
import net.obvj.smart.console.ManagementConsole;
import net.obvj.smart.jmx.AgentManagerJMX;
import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.manager.AgentManager;

/**
 * S.M.A.R.T. service main class
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class Main
{
    public static final Logger LOG = Logger.getLogger("smart-server");

    private static boolean runFlag = true;

    public static void main(String[] args)
    {
        /*
         * STEP 1: Start Agent Manager
         */
        LOG.info("Starting Agent Manager...");
        AgentManager manager = AgentManager.getInstance();

        /*
         * STEP 2: Start classic management console
         */
        if (SmartProperties.getInstance().getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED))
        {
            LOG.info("Starting classic Agent Management Console...");
            ManagementConsole.getInstance().start();
        }
        else
        {
            LOG.info("Classic Agent Management Console not enabled");
        }

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
        LOG.info("Loading agents configuration...");
        List<XmlAgent> xmlAgents = AgentConfiguration.getInstance().getAgents();

        if (xmlAgents.isEmpty())
        {
            System.exit(1);
        }

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

        LOG.log(Level.INFO, "Starting agents...");
        xmlAgents.stream().filter(XmlAgent::isAutomaticallyStarted)
                .forEach(xmlAgent -> manager.startAgent(xmlAgent.getName()));

        LOG.info("Ready.");

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
}
