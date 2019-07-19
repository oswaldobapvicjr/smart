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

public class AgentManagerJMXClient
{
    private static final Logger LOG = Logger.getLogger("smart");

    private static final String SERVICE_JMX_RMI_URL = "service:jmx:rmi:///jndi/rmi://:9999/jmxrmi";
    
    private static AgentManagerJMXMBean mbeanProxy;
    
    private AgentManagerJMXClient()
    {
        // No instances allowed
    }
    
    private static AgentManagerJMXMBean createMBeanProxy() throws IOException
    {
        try
        {
            JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(SERVICE_JMX_RMI_URL));
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName mbeanName = new ObjectName("net.obvj.smart.jmx:type=AgentManagerJMX");
            return JMX.newMBeanProxy(mbsc, mbeanName, AgentManagerJMXMBean.class, true);
        }
        catch (MalformedObjectNameException e)
        {
            LOG.log(Level.SEVERE, "Unable to find remote agent manager stub", e);
            return null;
        }
    }
    
    public static AgentManagerJMXMBean getMBeanProxy() throws IOException
    {
        if (mbeanProxy == null)
        {
            mbeanProxy = createMBeanProxy();
        }
        return mbeanProxy;
    }

}
