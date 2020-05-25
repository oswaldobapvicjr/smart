package net.obvj.smart.main;

import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.monitors.MonitoredRunnable;
import net.obvj.smart.conf.AgentConfigurationException;
import net.obvj.smart.jmx.JMXException;

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
     * @throws JMXException                if unable to register S.M.A.R.T. management beans
     * @throws AgentConfigurationException if unable to load agents configuration file
     */
    public void start()
    {
        startClassicManagementConsole();
        registerManagedBean();

        // Create shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(), "Shutdown"));

        // Start agents
        startAutomaticAgents();
    }

    public static void main(String[] args)
    {
        MonitoredRunnable operation = new MonitoredRunnable(() -> new Main().start());
        operation.run();
        LOG.info("Server started in {}", operation.getCounter(Type.WALL_CLOCK_TIME).elapsedTime());
    }

}
