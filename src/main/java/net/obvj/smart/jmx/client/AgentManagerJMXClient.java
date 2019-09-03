package net.obvj.smart.jmx.client;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import net.obvj.smart.jmx.AgentManagerJMXMBean;

/**
 * An object that contains infrastructure logic to connect to the S.M.A.R.T. server via
 * RMI and provides a proxy to the remote Agent Management bean
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentManagerJMXClient
{
    private static final Logger LOG = Logger.getLogger("smart-console");

    private static final String SERVICE_JMX_RMI_URL = "service:jmx:rmi:///jndi/rmi://:9999/jmxrmi";
    
    private static AgentManagerJMXMBean mbeanProxy;
    
    private AgentManagerJMXClient()
    {
        // No instances allowed
    }
    
    private static AgentManagerJMXMBean createMBeanProxy()
    {
        try
        {
            LOG.fine("Connecting to remote management console...");
            JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(SERVICE_JMX_RMI_URL));
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName mbeanName = new ObjectName("net.obvj.smart.jmx:type=AgentManagerJMX");
            return JMX.newMBeanProxy(mbsc, mbeanName, AgentManagerJMXMBean.class, true);
        }
        catch (MalformedObjectNameException | IOException e)
        {
            LOG.log(Level.SEVERE, "Unable to find remote agent manager stub", e);
            return null;
        }
    }
    
    public static AgentManagerJMXMBean getMBeanProxy()
    {
        if (mbeanProxy == null)
        {
            mbeanProxy = createMBeanProxy();
        }
        return mbeanProxy;
    }

}
