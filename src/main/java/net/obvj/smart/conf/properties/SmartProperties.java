package net.obvj.smart.conf.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * An object that maintains configuration data retrieved from the {@code smart.properties}
 * file
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Component
public class SmartProperties
{
    private static final Logger LOG = Logger.getLogger("smart-server");

    public static final String AGENT_SEARCH_PACKAGES = "agent.search.packages";
    protected static final String AGENT_SEARCH_PACKAGES_DEFAULT = "net.obvj.smart.agents.internal";

    public static final String CONSOLE_PROMPT = "console.prompt";
    protected static final String CONSOLE_PROMPT_DEFAULT = "smart>";

    public static final String CLASSIC_CONSOLE_ENABLED = "classic.console.enabled";
    protected static final String CLASSIC_CONSOLE_ENABLED_DEFAULT = "true";

    public static final String CLASSIC_CONSOLE_PORT = "classic.console.port";
    protected static final String CLASSIC_CONSOLE_PORT_DEFAULT = "1910";

    public static final String CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS = "classic.console.session.timeout.seconds";
    protected static final String CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS_DEFAULT = "60";

    public static final String JMX_REMOTE_PORT = "jmx.remote.port";
    protected static final String JMX_REMOTE_PORT_DEFAULT = "9999";

    public static final String JMX_REMOTE_AUTHENTICATE = "jmx.remote.authenticate";
    protected static final String JMX_REMOTE_AUTHENTICATE_DEFAULT = "false";

    public static final String JMX_REMOTE_SSL = "jmx.remote.ssl";
    protected static final String JMX_REMOTE_SSL_DEFAULT = "false";

    public static final String JMX_AGENT_MANAGER_OBJECT_NAME = "jmx.agent.manager.object.name";
    protected static final String JMX_OBJECT_NAME_DEFAULT = "net.obvj.smart.jmx:type=AgentManagerJMX";

    private static final Properties defaults = new Properties();
    static
    {
        defaults.put(AGENT_SEARCH_PACKAGES, AGENT_SEARCH_PACKAGES_DEFAULT);
        defaults.put(CONSOLE_PROMPT, CONSOLE_PROMPT_DEFAULT);
        defaults.put(CLASSIC_CONSOLE_ENABLED, CLASSIC_CONSOLE_ENABLED_DEFAULT);
        defaults.put(CLASSIC_CONSOLE_PORT, CLASSIC_CONSOLE_PORT_DEFAULT);
        defaults.put(CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS, CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS_DEFAULT);
        defaults.put(JMX_REMOTE_PORT, JMX_REMOTE_PORT_DEFAULT);
        defaults.put(JMX_REMOTE_AUTHENTICATE, JMX_REMOTE_AUTHENTICATE_DEFAULT);
        defaults.put(JMX_REMOTE_SSL, JMX_REMOTE_SSL_DEFAULT);
        defaults.put(JMX_AGENT_MANAGER_OBJECT_NAME, JMX_OBJECT_NAME_DEFAULT);
    }

    protected Properties properties = new Properties();

    public SmartProperties()
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

    /**
     * Returns a list of strings resulting from splitting the property associated with the
     * given key using the given regular expression. For example, considering the following
     * property:
     * <p>
     * {@code my.strings=foo,boo}
     * <p>
     * Calling {@code getPropertiesListSplitBy("my.strings", ",")} will return a list
     * containing the two strings: {@code foo} and {@code boo}.
     * 
     * @param key   the key to be fetched
     * @param regex the delimiting regular expression
     * @return the list of strings computed by splitting this property around matches of the
     *         given regular expression
     */
    public List<String> getPropertiesListSplitBy(String key, String regex)
    {
        String string = getProperty(key);
        if (StringUtils.isEmpty(string))
        {
            return Collections.emptyList();
        }
        return Arrays.stream(string.split(regex)).map(String::trim).collect(Collectors.toList());
    }

}
