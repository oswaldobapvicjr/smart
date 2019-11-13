package net.obvj.smart.console;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.conf.SmartProperties;
import net.obvj.smart.util.ApplicationContextFacade;

/**
 * A command-line user interface application for agents management at runtime. This
 * console should only be accessible locally.
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class ManagementConsole implements Runnable
{
    private static ManagementConsole instance = new ManagementConsole();

    private boolean started;
    private Thread serverThread;
    private ExecutorService sessionExecutor;
    private ServerSocket server;
    private final Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * @return A unique ManagementConsole instance
     */
    public static ManagementConsole getInstance()
    {
        return instance;
    }

    public ManagementConsole()
    {
        try
        {
            serverThread = new Thread(this, "MgmtConsole");
            sessionExecutor = Executors.newCachedThreadPool(new MgmtConsoleWorkerThreadFactory());
            server = new ServerSocket(getPort(), 0, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
            log.info("Management Console listening on port " + server.getLocalPort());
        }
        catch (IOException e)
        {
            log.log(Level.SEVERE, String.format("Unable to bind to system port %s.", getPort()), e);
        }
    }

    public int getPort()
    {
        return ApplicationContextFacade.getBean(SmartProperties.class).getIntProperty(SmartProperties.CLASSIC_CONSOLE_PORT);
    }

    public int getSessionTimeoutSeconds()
    {
        return ApplicationContextFacade.getBean(SmartProperties.class).getIntProperty(SmartProperties.CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS);
    }

    public void run()
    {
        while (started)
        {
            try
            {
                Socket socket = server.accept();
                socket.setSoTimeout(getSessionTimeoutSeconds() * 1000);
                log.info("Connection received from " + socket.getInetAddress().getHostName());
                sessionExecutor.submit(new CommandWorker(socket));
            }
            catch (SocketTimeoutException e)
            {
                log.info("Connection timeout");
            }
            catch (SocketException e)
            {
                if (server.isClosed()) return;
                log.warning(e.getMessage());
            }
            catch (Exception e)
            {
                log.severe(e.getMessage());
            }
        }
    }

    /**
     * Start the ManagementConsole on specified system port
     */
    public void start()
    {
        started = true;
        serverThread.start();
    }

    /**
     * Try to close gracefully open connections and stop the Management Console
     */
    public void stop()
    {
        started = false;
        sessionExecutor.shutdown();
        if (server != null && !server.isClosed())
        {
            try
            {
                server.close();
            }
            catch (IOException e)
            {
                log.log(Level.SEVERE, "Error closing server socket", e);
            }
        }
    }
    
}
