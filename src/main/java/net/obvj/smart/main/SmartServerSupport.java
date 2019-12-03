package net.obvj.smart.main;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.ObjectName;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.conf.properties.SmartProperties;
import net.obvj.smart.console.ManagementConsole;
import net.obvj.smart.jmx.AgentManagerJMX;
import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.ApplicationContextFacade;

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

    protected String jmxAgentManagerObjectName = smartProperties
            .getProperty(SmartProperties.JMX_AGENT_MANAGER_OBJECT_NAME);

    protected static final Logger LOG = Logger.getLogger("smart-server");


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
            LOG.fine("Classic Management Console not enabled");
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
        LOG.log(Level.INFO, "Starting agents...");
        agentManager.getAgents().stream().filter(Agent::isAutomaticallyStarted).forEach(this::startAgent);
    }

    protected void startAgent(Agent agent)
    {
        agentManager.startAgent(agent.getName());
    }

    protected void registerManagedBean() throws JMException
    {
        LOG.info("Creating and registering Managed Beans...");
        AgentManagerJMXMBean mBean = new AgentManagerJMX();
        ObjectName name = new ObjectName(jmxAgentManagerObjectName);
        ManagementFactory.getPlatformMBeanServer().registerMBean(mBean, name);
    }

}
