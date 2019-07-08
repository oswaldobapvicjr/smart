package net.obvj.smart.console;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * A command-line user interface application for agents management at runtime. This
 * console should only be accessible locally.
 */
public class ManagementConsole implements Runnable
{

    public static final int PORT = 1910;
    public static final int SESSION_TIMEOUT_MILIS = 60000;
    private static ManagementConsole instance = new ManagementConsole();
    private boolean itShouldRun;
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

    private ManagementConsole()
    {
        try
        {
            server = new ServerSocket(PORT, 0, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
            log.info("Management Console listening on port " + server.getLocalPort());
            serverThread = new Thread(this, "MgmtConsole");
            sessionExecutor = Executors.newCachedThreadPool(new MgmtConsoleSessionThreadFactory());

        }
        catch (BindException e)
        {
            log.severe("Unable to open system port " + PORT + ". Please make sure this port is not in use.");
        }
        catch (IOException e)
        {
            log.severe(e.getMessage());
        }
    }

    public void run()
    {
        while (itShouldRun)
        {
            try
            {
                Socket socket = server.accept();
                socket.setSoTimeout(SESSION_TIMEOUT_MILIS);
                log.info("Connection received from " + socket.getInetAddress().getHostName());
                sessionExecutor.submit(new CommandWorker(socket));

            }
            catch (SocketTimeoutException e)
            {
                log.info("Connection timeout");
            }
            catch (SocketException e)
            {
                if (server.isClosed())
                {
                    return;
                }
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
        itShouldRun = true;
        serverThread.start();
    }

    /**
     * Try to close gracefully open connections and stop the Management Console
     */
    public void stop()
    {
        itShouldRun = false;
        sessionExecutor.shutdown();
        if (server != null && !server.isClosed())
        {
            try
            {
                server.close();
            }
            catch (IOException e)
            {
                log.severe("Error closing server socket on port " + PORT);
            }
        }
    }

    static class MgmtConsoleSessionThreadFactory implements ThreadFactory
    {

        static final String namePrefix = "MgmtConsoleWorker-T";
        static final AtomicInteger threadNumber = new AtomicInteger(1);
        final ThreadGroup group;

        MgmtConsoleSessionThreadFactory()
        {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        public Thread newThread(Runnable runnable)
        {
            String name = namePrefix + threadNumber.getAndIncrement();
            Thread thread = new Thread(group, runnable, name);
            thread.setPriority(Thread.MIN_PRIORITY);
            if (thread.isDaemon())
            {
                thread.setDaemon(false);
            }
            return thread;
        }
    }
}
