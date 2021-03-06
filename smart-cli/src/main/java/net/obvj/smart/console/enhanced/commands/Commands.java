package net.obvj.smart.console.enhanced.commands;

import java.io.PrintWriter;

import jline.console.ConsoleReader;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * A base command that groups all commands available to the Enhanced Management Console
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "", synopsisHeading = "", synopsisSubcommandLabel = "",
         commandListHeading = "Available commands:%n%n",
         subcommands = { AgentsCommand.class,
                         ClearScreenCommand.class,
                         DateCommand.class,
                         JavaVersionCommand.class,
                         StatusCommand.class,
                         StartCommand.class,
                         StopCommand.class,
                         ResetCommand.class,
                         RunCommand.class,
                         ThreadsCommand.class,
                         UptimeCommand.class },
         footer = { "", "Press <Ctrl> + D to exit." })
public class Commands implements Runnable
{
    private final ConsoleReader reader;
    protected final PrintWriter out;

    public Commands(ConsoleReader reader)
    {
        this.reader = reader;
        out = new PrintWriter(reader.getOutput());
    }

    protected Commands(PrintWriter out)
    {
        this.reader = null;
        this.out = out;
    }

    @Override
    public void run()
    {
        out.println(new CommandLine(this).getUsageMessage());
    }

    protected ConsoleReader getConsoleReader()
    {
        return reader;
    }
}
