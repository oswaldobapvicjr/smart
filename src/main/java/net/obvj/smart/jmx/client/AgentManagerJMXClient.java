package net.obvj.smart.jmx.client;

import java.io.IOException;
import java.net.MalformedURLException;
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
    private static final String SERVICE_JMX_RMI_URL = "service:jmx:rmi:///jndi/rmi://:9999/jmxrmi";

    private static final AgentManagerJMXClient instance = new AgentManagerJMXClient();
    
    private AgentManagerJMXMBean mbeanProxy;
    
    private AgentManagerJMXClient()
    {
        try
        {
            JMXConnector jmxc = JMXConnectorFactory.connect(getUrl());
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

            ObjectName mbeanName = new ObjectName("net.obvj.smart.jmx:type=AgentManagerJMX");
            mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, AgentManagerJMXMBean.class, true);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedObjectNameException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private JMXServiceURL getUrl() throws MalformedURLException
    {
        return new JMXServiceURL(SERVICE_JMX_RMI_URL);
    }
    
    public AgentManagerJMXMBean getMBeanProxy()
    {
        return mbeanProxy;
    }
    
    public static AgentManagerJMXClient getInstance()
    {
        return instance;
    }

}
