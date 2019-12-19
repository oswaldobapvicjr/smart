package net.obvj.smart.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Utility methods for the management console
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ConsoleUtils
{
    private static final Logger LOG = Logger.getLogger("smart-commons");
    private static final String HEADER_FILE = "header.txt";

    private ConsoleUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Read all lines from the custom header file.
     *
     * @return the lines from the file as a {@link List}
     */
    public static List<String> readCustomHeaderLines()
    {
        LOG.fine("Searching custom header file...");
        return readFileLines(HEADER_FILE);
    }

    /**
     * Read all lines from an path in the class path, decoded as UTF-8.
     *
     * @param path the absolute path within the class path
     * @return the lines from the file as a {@link List}
     */
    public static List<String> readFileLines(String path)
    {
        LOG.log(Level.FINE, "Searching file: {0}", path);
        try
        {
            Resource resource = new ClassPathResource(path);
            if (resource.exists())
            {
                LOG.log(Level.FINE, "{0} found", path);
                return Files.readAllLines(Paths.get(resource.getURI()));
            }
            else
            {
                LOG.log(Level.WARNING, "Unable to find {0}", path);
            }
        }
        catch (IOException e)
        {
            LOG.log(Level.WARNING, "Error reading file: {0} ({1}: {2})",
                    new String[] { path, e.getClass().getName(), e.getLocalizedMessage() });
        }
        return Collections.emptyList();
    }

}
