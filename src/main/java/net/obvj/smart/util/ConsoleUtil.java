package net.obvj.smart.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods for the management console
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ConsoleUtil
{
    private static final Logger LOG = Logger.getLogger("smart-server");

    private ConsoleUtil()
    {
        throw new IllegalStateException("Utility class");
    }
    
    public static List<String> readCustomHeaderLines()
    {
        LOG.fine("Searching for custom header file...");
        try
        {
            URL headerFileURL = ConsoleUtil.class.getClassLoader().getResource("header.txt");
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
}
