package net.obvj.smart.console.enhanced.commands;

import java.io.IOException;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/**
 * A command that clears the console
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "clear", aliases = "cls",
         headerHeading = "@|bold,underline Usage|@:%n%n",
         header = "Clear the screen",
         synopsisHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n%n",
         description = "Clear the screen")
public class ClearScreenCommand implements Callable<Void>
{
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;
    
    @ParentCommand
    private Commands parent;

    @Override
    public Void call() throws IOException
    {
        parent.getConsoleReader().clearScreen();
        return null;
    }

    protected void setParent(Commands parent)
    {
        this.parent = parent;
    }
}
