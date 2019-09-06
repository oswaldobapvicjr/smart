package net.obvj.smart.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An object that maintains configuration data retrieved from the {@code smart.properties} file
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class SmartProperties
{
    private static final Logger LOG = Logger.getLogger("smart-server");

    public static final String CONSOLE_PROMPT = "console.prompt";
    protected static final String CONSOLE_PROMPT_DEFAULT = "smart>";

    public static final String CLASSIC_CONSOLE_ENABLED = "classic.console.enabled";
    protected static final String CLASSIC_CONSOLE_ENABLED_DEFAULT = "true";

    public static final String CLASSIC_CONSOLE_PORT = "classic.console.port";
    protected static final String CLASSIC_CONSOLE_PORT_DEFAULT = "1910";

    public static final String CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS = "classic.console.session.timeout.seconds";
    protected static final String CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS_DEFAULT = "60";

    private static final Properties defaults = new Properties();
    static
    {
        defaults.put(CONSOLE_PROMPT, CONSOLE_PROMPT_DEFAULT);
        defaults.put(CLASSIC_CONSOLE_ENABLED, CLASSIC_CONSOLE_ENABLED_DEFAULT);
        defaults.put(CLASSIC_CONSOLE_PORT, CLASSIC_CONSOLE_PORT_DEFAULT);
        defaults.put(CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS, CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS_DEFAULT);
    }

    private static final SmartProperties INSTANCE = new SmartProperties();

    protected Properties properties = new Properties();

    private SmartProperties()
    {
        this("smart.properties");
    }
    
    protected SmartProperties(String fileName)
    {
        try
        {
            properties = readPropertiesFile(fileName);
        }
        catch (NullPointerException e)
        {
            LOG.log(Level.WARNING, "{0} not found. Using default properties...", fileName);
        }
        catch (IOException e)
        {
            LOG.log(Level.WARNING, "Unable to read {0}. Using default properties. Error details: {1} ({2})",
                    new String[] { fileName, e.getClass().getName(), e.getLocalizedMessage() });
        }
    }

    /**
     * Loads properties from a given file name.
     * 
     * @param fileName the file name to be loaded
     * @return a Properties object, filled with the content parsed from the given file
     * @throws NullPointerException if the file is not found in the class path
     * @throws IOException          if unable to parse properties file content
     */
    private static Properties readPropertiesFile(String fileName) throws IOException
    {
        LOG.log(Level.FINE, "Searching for {0} file...", fileName);
        try (final InputStream stream = SmartProperties.class.getClassLoader().getResourceAsStream(fileName))
        {
            Properties properties = new Properties();
            properties.load(stream);
            LOG.log(Level.FINE, "{0} loaded successfully", fileName);
            return properties;
        }
    }

    public static SmartProperties getInstance()
    {
        return INSTANCE;
    }

    public String getProperty(String key)
    {
        return properties.getProperty(key, defaults.getProperty(key));
    }

    public int getIntProperty(String key)
    {
        return Integer.parseInt(getProperty(key));
    }

    public boolean getBooleanProperty(String key)
    {
        return Boolean.parseBoolean(getProperty(key));
    }
}
