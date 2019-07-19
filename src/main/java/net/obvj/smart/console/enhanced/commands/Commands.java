package net.obvj.smart.console.enhanced.commands;

import java.io.PrintWriter;

import jline.console.ConsoleReader;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "", header = "",
         commandListHeading = "Available commands:%n%n",
         subcommands = { AgentsCommand.class,
                         DateCommand.class,
                         StatusCommand.class,
                         StartCommand.class,
                         StopCommand.class,
                         ResetCommand.class,
                         RunCommand.class,
                         ThreadsCommand.class,
                         UptimeCommand.class },
         footer = { "", "Press Ctl+D to exit." })
public class Commands implements Runnable
{
    protected final ConsoleReader reader;
    protected final PrintWriter out;

    public Commands(ConsoleReader reader)
    {
        this.reader = reader;
        out = new PrintWriter(reader.getOutput());
    }

    public void run()
    {
        out.println(new CommandLine(this).getUsageMessage());
    }
}
