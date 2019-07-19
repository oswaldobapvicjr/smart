package net.obvj.smart.console.enhanced;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter.ArgumentList;
import jline.console.completer.ArgumentCompleter.WhitespaceArgumentDelimiter;
import net.obvj.smart.console.enhanced.commands.Commands;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine;
import picocli.shell.jline2.PicocliJLineCompleter;

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
            
            // Start the shell and process input until the user quits with Ctl+D
            String line;
            while ((line = args !=null && args.length > 0 ? args[0] : reader.readLine()) != null)
            {
                line = line.trim();
                if (!"".equals(line))
                {
                    if ("exit".equals(line))
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

    public static void main(String[] args)
    {
        new EnhancedManagementConsole(args).run();
    }
}
