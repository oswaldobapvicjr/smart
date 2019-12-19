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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.obvj.smart.conf.properties.SmartProperties;
import net.obvj.smart.jmx.AgentManagerJMXMBean;

/**
 * An object that contains infrastructure logic to connect to the S.M.A.R.T. server via
 * RMI and provides a proxy to the remote Agent Management bean
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Component
public class AgentManagerJMXClient
{
    private static final Logger LOG = Logger.getLogger("smart-cli");

    private static final String SERVICE_JMX_RMI_URL = "service:jmx:rmi:///jndi/rmi://:%s/jmxrmi";

    @Autowired
    private SmartProperties smartProperties;

    private AgentManagerJMXMBean mbeanProxy;

    private AgentManagerJMXMBean createMBeanProxy()
    {
        try
        {
            LOG.fine("Connecting to remote management console...");
            JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(String.format(SERVICE_JMX_RMI_URL, getJMXRemotePort())));
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            return JMX.newMBeanProxy(mbsc, getAgentManagerJMXBeanObjectName(), AgentManagerJMXMBean.class, true);
        }
        catch (MalformedObjectNameException | IOException e)
        {
            LOG.log(Level.SEVERE, "Unable to find remote agent manager stub", e);
            return null;
        }
    }

    protected int getJMXRemotePort()
    {
        return smartProperties.getIntProperty(SmartProperties.JMX_REMOTE_PORT);
    }

    protected ObjectName getAgentManagerJMXBeanObjectName() throws MalformedObjectNameException
    {
        return new ObjectName(smartProperties.getProperty(SmartProperties.JMX_AGENT_MANAGER_OBJECT_NAME));
    }

    public AgentManagerJMXMBean getMBeanProxy()
    {
        if (mbeanProxy == null)
        {
            mbeanProxy = createMBeanProxy();
        }
        return mbeanProxy;
    }

}
