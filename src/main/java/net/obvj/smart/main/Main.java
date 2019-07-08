package net.obvj.smart.main;

import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.OperationsException;

import net.obvj.smart.agents.dummy.DummyAgent;
import net.obvj.smart.agents.dummy.PMReporter;
import net.obvj.smart.console.ManagementConsole;
import net.obvj.smart.jmx.AgentManagerJMX;
import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.manager.AgentManager;

/**
 * To make an application is visible to JConsole, simply add this JVM argument to the
 * startup: -Dcom.sun.management.jmxremote
 */
public class Main
{

    public static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args)
    {

        /*
         * STEP 1: Start Agent Manager
         */
        logger.info("Starting Agent Manager...");
        AgentManager manager = AgentManager.getInstance();

        /*
         * STEP 2: Start management console
         */
        logger.info("Starting Agent Management Console...");
        ManagementConsole.getInstance().start();

        /*
         * STEP 3: Register Managed Beans
         */
        logger.info("Creating and registering Managed Beans...");
        try
        {
            AgentManagerJMXMBean mBean = new AgentManagerJMX();
            ObjectName name = new ObjectName("com.agent.jmx:type=AgentManagerJMX");
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            mBeanServer.registerMBean(mBean, name);

        }
        catch (OperationsException | MBeanRegistrationException e)
        {
            e.printStackTrace();
        }

        /*
         * STEP 4: Create shutdown hook
         */
        logger.info("Creating shutdown hook...");
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(), "Shutdown"));

        /*
         * Step 5: Register and start agents
         */
        logger.info("Loading agents...");
        manager.addAgent(new DummyAgent("DummyAgent"));
        manager.addAgent(new PMReporter("PMReporter"));
        manager.startAgent("DummyAgent");
        manager.startAgent("PMReporter");

        logger.info("Ready.");

    }
}
