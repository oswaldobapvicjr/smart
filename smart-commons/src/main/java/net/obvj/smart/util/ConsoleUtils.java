package net.obvj.smart.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Utility methods for the management console.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class ConsoleUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(ConsoleUtils.class);
    private static final String HEADER_FILE = "header.txt";

    private ConsoleUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Reads all lines from the custom header file.
     *
     * @return the lines from the file as a {@link List}
     */
    public static List<String> readCustomHeaderLines()
    {
        LOG.debug("Searching custom header file...");
        return readFileLines(HEADER_FILE);
    }

    /**
     * Reads all lines from an path in the class path, decoded as UTF-8.
     *
     * @param path the absolute path within the class path
     * @return the lines from the file as a {@link List}
     */
    public static List<String> readFileLines(String path)
    {
        LOG.debug("Searching file: {} ...", path);
        try
        {
            Resource resource = new ClassPathResource(path);
            if (resource.exists())
            {
                LOG.debug("{} found", path);
                return Files.readAllLines(Paths.get(resource.getURI()));
            }
            else
            {
                LOG.warn("Unable to find {}", path);
            }
        }
        catch (IOException exception)
        {
            LOG.warn("Error reading file: {} ({}: {})", path, exception.getClass().getName(),
                    exception.getLocalizedMessage());
        }
        return Collections.emptyList();
    }

}
