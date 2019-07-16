package net.obvj.smart.console.enhanced;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter.ArgumentList;
import jline.console.completer.ArgumentCompleter.WhitespaceArgumentDelimiter;
import net.obvj.smart.console.enhanced.commands.Commands;
import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine;
import picocli.shell.jline2.PicocliJLineCompleter;

public class EnhancedManagementConsole implements Runnable
{
    private static final Logger LOG = Logger.getLogger("smart");

    @Override
    public void run()
    {
        AgentManagerJMXClient.getInstance();
        try
        {
            ConsoleReader reader = new ConsoleReader();
            reader.setPrompt("smart> ");

            // Set up the completion
            Commands commands = new Commands(reader);
            CommandLine cmd = new CommandLine(commands);
            reader.addCompleter(new PicocliJLineCompleter(cmd.getCommandSpec()));

            // Start the shell and process input until the user quits with Ctl+D
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (!"".equals(line))
                {
                    if ("exit".equals(line))
                    {
                        break;
                    }

                    // Handle command line
                    ArgumentList list = new WhitespaceArgumentDelimiter().delimit(line, line.length());
                    CommandLine.run(commands, list.getArguments());
                    reader.println();
                }
            }
        }
        catch (IOException e)
        {
            LOG.log(Level.SEVERE, "Enhanced Management Console service ended unexpectedly", e);
        }
    }

    public static void main(String[] args)
    {
        new EnhancedManagementConsole().run();
    }
}
