package net.obvj.smart.jmx.client;

import java.io.IOException;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(AgentManagerJMXClient.class);

    private static final String SERVICE_JMX_RMI_URL = "service:jmx:rmi:///jndi/rmi://:%s/jmxrmi";

    @Autowired
    private SmartProperties smartProperties;

    private AgentManagerJMXMBean mbeanProxy;

    private AgentManagerJMXMBean createMBeanProxy()
    {
        try
        {
            LOG.info("Connecting to the S.M.A.R.T. server...");

            JMXConnector jmxc = JMXConnectorFactory.connect(new JMXServiceURL(String.format(SERVICE_JMX_RMI_URL, getJMXRemotePort())));
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
            ObjectName agentManagerJMXBeanObjectName = getAgentManagerJMXBeanObjectName();
            AgentManagerJMXMBean newMBeanProxy = JMX.newMBeanProxy(mbsc, agentManagerJMXBeanObjectName, AgentManagerJMXMBean.class, true);

            LOG.info("Connected");
            return newMBeanProxy;
        }
        catch (MalformedObjectNameException | IOException e)
        {
            LOG.error("Unable to find remote agent manager stub", e);
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
