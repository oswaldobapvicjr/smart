package net.obvj.smart.console;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.obvj.smart.conf.properties.SmartProperties;
import net.obvj.smart.util.Exceptions;

/**
 * A command-line user interface application for agents management at runtime. This
 * console should only be accessible locally.
 *
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
@Component
public class ManagementConsole implements Runnable
{
    private static final Logger LOG = LoggerFactory.getLogger(ManagementConsole.class);

    @Autowired
    private SmartProperties smartProperties;

    private boolean started;
    private Thread serverThread;
    private ExecutorService sessionExecutor;
    private ServerSocket server;

    public ManagementConsole()
    {
        serverThread = new Thread(this, "MgmtConsole");
        sessionExecutor = Executors.newCachedThreadPool(new MgmtConsoleWorkerThreadFactory());
    }

    public int getPort()
    {
        return smartProperties.getIntProperty(SmartProperties.CLASSIC_CONSOLE_PORT);
    }

    public int getSessionTimeoutSeconds()
    {
        return smartProperties.getIntProperty(SmartProperties.CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS);
    }

    @Override
    public void run()
    {
        while (started)
        {
            try
            {
                Socket socket = server.accept();
                socket.setSoTimeout(getSessionTimeoutSeconds() * 1000);
                LOG.info("Connection received from {}", socket.getInetAddress().getHostName());
                sessionExecutor.submit(new CommandWorker(socket));
            }
            catch (SocketTimeoutException e)
            {
                LOG.info("Connection timeout");
            }
            catch (SocketException exception)
            {
                if (server.isClosed()) return;
                LOG.warn(exception.getMessage());
            }
            catch (Exception exception)
            {
                LOG.error(exception.getMessage());
            }
        }
    }

    /**
     * Start the ManagementConsole on specified system port
     */
    public void start()
    {
        try
        {
            started = true;
            server = new ServerSocket(getPort(), 0, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
            serverThread.start();
            LOG.info("Management Console listening to port {}", server.getLocalPort());
        }
        catch (IOException exception)
        {
            Exceptions.illegalState(exception, "Unable to bind to system port %s", getPort());
        }
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
            catch (IOException exception)
            {
                LOG.error("Error closing server socket", exception);
            }
        }
    }

}
