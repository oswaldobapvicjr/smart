package net.obvj.smart.conf;

import java.util.Properties;

public class SmartProperties
{

    public static final String CONSOLE_PROMPT = "console.prompt";
    private static final String CONSOLE_PROMPT_DEFAULT = "smart>";

    public static final String CLASSIC_CONSOLE_ENABLED = "classic.console.enabled";
    private static final String CLASSIC_CONSOLE_ENABLED_DEFAULT = "true";
    
    public static final String CLASSIC_CONSOLE_PORT = "classic.console.port";
    private static final String CLASSIC_CONSOLE_PORT_DEFAULT = "1910";

    public static final String CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS = "classic.console.session.timeout.seconds";
    private static final String CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS_DEFAULT = "60";
    
    private static final Properties defaults = new Properties();
    static
    {
        defaults.put(CONSOLE_PROMPT, CONSOLE_PROMPT_DEFAULT);
        defaults.put(CLASSIC_CONSOLE_ENABLED, CLASSIC_CONSOLE_ENABLED_DEFAULT);
        defaults.put(CLASSIC_CONSOLE_PORT, CLASSIC_CONSOLE_PORT_DEFAULT);
        defaults.put(CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS, CLASSIC_CONSOLE_SESSION_TIMEOUT_SECONDS_DEFAULT);
    }

    private static final SmartProperties instance = new SmartProperties();

    private Properties properties = new Properties();

    private SmartProperties()
    {
    }

    public static SmartProperties getInstance()
    {
        return instance;
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
