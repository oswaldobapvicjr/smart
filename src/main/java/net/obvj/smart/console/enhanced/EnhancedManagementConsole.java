package net.obvj.smart.console.enhanced;

import java.io.IOException;
import java.io.PrintWriter;
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
    private static final Logger LOG = Logger.getLogger("smart");
    private String[] args;

    public EnhancedManagementConsole(String[] args)
    {
        this.args = args;
    }
    
    @Override
    public void run()
    {
        try
        {
            // Setting-up the console
            ConsoleReader reader = new ConsoleReader();
            reader.setPrompt("smart> ");

            // Set up the completion
            Commands commands = new Commands(reader);
            CommandLine cmd = new CommandLine(commands);
            reader.addCompleter(new PicocliJLineCompleter(cmd.getCommandSpec()));

            // Set up connection to JMX
            AgentManagerJMXClient.getMBeanProxy();
            
            // Print custom header and hints
            printHeader(reader);

            // Start the shell and process input until the user quits with Ctl+D
            String line;
            while ((line = args !=null && args.length > 0 ? args[0] : reader.readLine()) != null)
            {
                line = line.trim();
                if (!"".equals(line))
                {
                    if ("exit".equals(line) || "quit".equals(line))
                    {
                        break;
                    }
                    handleCommandLine(reader, commands, line);
                }
            }
        }
        catch (IOException e)
        {
            LOG.log(Level.SEVERE, "Enhanced Management Console service ended unexpectedly", e);
        }
    }

    private void handleCommandLine(ConsoleReader reader, Commands commands, String line) throws IOException
    {
        try
        {
            ArgumentList list = new WhitespaceArgumentDelimiter().delimit(line, line.length());
            new CommandLine(commands).execute(list.getArguments());
            reader.println();
        }
        catch (Exception e) {
            reader.println(e.getClass().getName() + ": " + e.getMessage() + "\n");
        }
    }

    private void printHeader(ConsoleReader console)
    {
        // Print custom header
        List<String> customHeaderLines = readCustomHeaderLines();
        PrintWriter out = new PrintWriter(console.getOutput());
        customHeaderLines.forEach(out::println);
        out.flush();
        
        // Print hints
        out.println();
        out.println(" Hit <Tab> for a list of available commands.");
        out.println(" Press <Ctrl> + D to quit the console.");
        out.println();
        out.flush();
    }
    
    private List<String> readCustomHeaderLines()
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
    
    public static void main(String[] args)
    {
        new EnhancedManagementConsole(args).run();
    }
}
