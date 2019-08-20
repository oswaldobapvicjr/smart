package net.obvj.smart.console.enhanced;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter.ArgumentList;
import jline.console.completer.ArgumentCompleter.WhitespaceArgumentDelimiter;
import net.obvj.smart.conf.SmartProperties;
import net.obvj.smart.console.CommandWorker;
import net.obvj.smart.console.enhanced.commands.Commands;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine;
import picocli.shell.jline2.PicocliJLineCompleter;

/**
 * A new management console with enhanced user experience features, such as
 * auto-completion and history navigation, and runs in a separate JVM instance.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class EnhancedManagementConsole implements Runnable
{
    public enum Mode
    {
        /**
         * Interactive console: start the shell and process user input until the user hits Ctrl+D
         */
        INTERACTIVE,

        /**
         * Executes a single command received via application arguments and quits
         */
        SINGLE_COMMAND;
    }

    private static final Logger LOG = Logger.getLogger("smart");
    private static final String PROMPT = SmartProperties.getInstance().getProperty(SmartProperties.CONSOLE_PROMPT) + " ";

    private final String[] args;
    private final ConsoleReader reader;
    private final Commands commands;
    private final Mode mode;

    public EnhancedManagementConsole(String... args) throws IOException
    {
        this.args = args;
        mode = userArgsSet(args) ? Mode.SINGLE_COMMAND : Mode.INTERACTIVE;

        // Setting-up the console
        reader = new ConsoleReader();
        reader.setPrompt(PROMPT);

        // Set up the completion
        commands = new Commands(reader);
        CommandLine cmd = new CommandLine(commands);
        reader.addCompleter(new PicocliJLineCompleter(cmd.getCommandSpec()));
    }

    private boolean userArgsSet(String... args)
    {
        return args != null && args.length > 0 && args[0] != null && !"".equals(args[0]);
    }

    @Override
    public void run()
    {
        try
        {
            // Set up connection to JMX
            AgentManagerJMXClient.getMBeanProxy();

            // Print custom header and hints
            printHeader(reader.getOutput());

            if (mode == Mode.SINGLE_COMMAND)
            {
                handleCommandLine(parseArgs(args));
            }
            else
            {
                // Start the shell and process user input until the user quits with Ctl+D
                String line;
                while ((line = reader.readLine()) != null)
                {
                    line = line.trim();
                    if (!"".equals(line))
                    {
                        if ("exit".equals(line) || "quit".equals(line))
                        {
                            break;
                        }
                        handleCommandLine(line);
                    }
                }
            }

        }
        catch (IOException e)
        {
            LOG.log(Level.SEVERE, "Enhanced Management Console service ended unexpectedly", e);
        }
    }

    protected static String parseArgs(String... args)
    {
        return String.join(" ", args);
    }

    protected void handleCommandLine(String line) throws IOException
    {
        try
        {
            ArgumentList list = new WhitespaceArgumentDelimiter().delimit(line, line.length());
            new CommandLine(commands).execute(list.getArguments());
            reader.println();
        }
        catch (Exception e)
        {
            reader.println(e.getClass().getName() + ": " + e.getMessage() + "\n");
        }
        finally
        {
            reader.flush();
        }
    }

    protected void printHeader(Writer writer)
    {
        // Print custom header
        List<String> customHeaderLines = readCustomHeaderLines();
        PrintWriter out = new PrintWriter(writer);
        customHeaderLines.forEach(out::println);
        out.println();

        if (mode == Mode.INTERACTIVE)
        {
            // Print hints
            out.println(" Hit <Tab> for a list of available commands.");
            out.println(" Press <Ctrl> + D to quit the console.");
            out.println();
        }
        out.flush();
    }

    protected List<String> readCustomHeaderLines()
    {
        LOG.fine("Searching for custom header file...");
        try
        {
            URL headerFileURL = CommandWorker.class.getClassLoader().getResource("header.txt");
            if (headerFileURL == null)
            {
                LOG.warning("Unable to find header.txt file");
                return Collections.emptyList();
            }
            return Files.readAllLines(Paths.get(headerFileURL.toURI()));
        }
        catch (URISyntaxException | IOException e)
        {
            LOG.log(Level.WARNING, "Unable to read header.txt file: {0} ({1})",
                    new String[] { e.getClass().getName(), e.getLocalizedMessage() });
            return Collections.emptyList();
        }
    }

    public static void main(String[] args) throws IOException
    {
        new EnhancedManagementConsole(args).run();
    }

    protected Mode getMode()
    {
        return mode;
    }

}
