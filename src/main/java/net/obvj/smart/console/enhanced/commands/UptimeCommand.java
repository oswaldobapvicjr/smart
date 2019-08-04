package net.obvj.smart.console.enhanced.commands;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

/**
 * A command that prints the total server up-time.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Command(name = "uptime",
         headerHeading = "@|bold,underline Usage|@:%n%n",
         header = "Display server up-time",
         synopsisHeading = "%n",
         descriptionHeading = "%n@|bold,underline Description|@:%n%n",
         description = "Retrieve total server up-time.",
         parameterListHeading = "%n@|bold,underline Parameters|@:%n",
         optionListHeading = "%n@|bold,underline Options|@:%n")
public class UptimeCommand implements Runnable
{
    @Option(names = { "-h", "--help" }, usageHelp = true, description = "Display this help message.")
    boolean usageHelpRequested;
    
    @Option(names = { "-t", "--time-unit" },
            description = "The time unit to be displayed (default = milliseconds)%n"
                    + "Possible values: <milliseconds|seconds|minutes|hours|days>")
    private String timeUnit = "milliseconds";
    
    @ParentCommand
    private Commands parent;

    @Override
    public void run()
    {
        try
        {
            long serverUptimeMillis = AgentManagerJMXClient.getMBeanProxy().getServerUptime();
            TimeUnit tu = TimeUnit.valueOf(timeUnit.toUpperCase());
            long serverUptimeConverted = tu.convert(serverUptimeMillis, TimeUnit.MILLISECONDS);

            String timeUnitToBeDisplayed = serverUptimeConverted > 1 ? tu.toString().toLowerCase()
                    : tu.toString().toLowerCase().substring(0, tu.toString().length() - 1);

            parent.out.println(
                    (serverUptimeConverted > 0 ? serverUptimeConverted : "Less than 1") + " " + timeUnitToBeDisplayed);
        }
        catch (IOException e)
        {
            parent.out.println("Unable to connect to the agent manager. Please make sure the service is running.");
        }
    }
}
