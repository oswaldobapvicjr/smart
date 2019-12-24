package net.obvj.smart.main;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import net.obvj.performetrics.runnable.WallClockTimeRunnableOperation;
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
        WallClockTimeRunnableOperation operation = new WallClockTimeRunnableOperation(() -> new Main().start());
        operation.run();
        LOG.log(Level.INFO, "Server started in {0} milliseconds", operation.elapsedTime(TimeUnit.MILLISECONDS));
    }

}
