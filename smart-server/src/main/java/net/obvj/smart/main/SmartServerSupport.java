package net.obvj.smart.main;

import java.lang.management.ManagementFactory;

import javax.management.JMException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.obvj.smart.agents.Agent;
import net.obvj.smart.conf.properties.SmartProperties;
import net.obvj.smart.console.ManagementConsole;
import net.obvj.smart.jmx.AgentManagerJMX;
import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.ApplicationContextFacade;
import net.obvj.smart.util.Exceptions;

/**
 * S.M.A.R.T. server support methods
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class SmartServerSupport
{
    protected SmartProperties smartProperties = ApplicationContextFacade.getBean(SmartProperties.class);
    protected AgentManager agentManager = ApplicationContextFacade.getBean(AgentManager.class);
    protected ManagementConsole managementConsole = ApplicationContextFacade.getBean(ManagementConsole.class);

    protected static final Logger LOG = LoggerFactory.getLogger(SmartServerSupport.class);

    public boolean isClassicConsoleEnabled()
    {
        return smartProperties.getBooleanProperty(SmartProperties.CLASSIC_CONSOLE_ENABLED);
    }

    protected void startClassicManagementConsole()
    {
        if (isClassicConsoleEnabled())
        {
            LOG.info("Starting Classic Management Console...");
            managementConsole.start();
        }
        else
        {
            LOG.info("Classic Management Console not enabled");
        }
    }

    protected void closeClassicManagementConsole()
    {
        if (isClassicConsoleEnabled())
        {
            LOG.info("Closing Classic Management Console...");
            managementConsole.stop();
            LOG.info("Classic Management Console closed");
        }
    }

    protected void startAutomaticAgents()
    {
        LOG.info("Starting agents...");
        agentManager.getAgents().stream().filter(Agent::isAutomaticallyStarted).forEach(this::startAgent);
    }

    protected void startAgent(Agent agent)
    {
        agentManager.startAgent(agent.getName());
    }

    protected void registerManagedBean()
    {
        String jmxAgentManagerObjectName = smartProperties.getProperty(SmartProperties.JMX_AGENT_MANAGER_OBJECT_NAME);
        registerManagedBean(jmxAgentManagerObjectName);
    }

    protected void registerManagedBean(String objectName)
    {
        LOG.info("Creating and registering Managed Bean {}", objectName);
        AgentManagerJMXMBean mBean = new AgentManagerJMX();

        try
        {
            ObjectName name = new ObjectName(objectName);
            ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, name);
        }
        catch (JMException cause)
        {
            throw Exceptions.jmx(cause, "Unable to register Managed Bean: %s", objectName);
        }

    }

}
