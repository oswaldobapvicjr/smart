package net.obvj.smart.console.enhanced.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.obvj.smart.jmx.AgentManagerJMXMBean;
import net.obvj.smart.jmx.client.AgentManagerJMXClient;
import net.obvj.smart.util.ClientApplicationContextFacade;

/**
 * A class that allows Picocli to auto-complete S.M.A.R.T. agents names.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentCompletionCandidates extends ArrayList<String>
{
    private static final String MSG_UNABLE_TO_RETRIEVE_NAMES = "Unable to retrieve available agent names";

    private static final long serialVersionUID = 3919528430528865666L;

    private static final Logger LOG = LoggerFactory.getLogger("smart-cli");

    public static List<String> getAgentNames()
    {
        try
        {
            AgentManagerJMXClient jmxClient = ClientApplicationContextFacade.getBean(AgentManagerJMXClient.class);
            AgentManagerJMXMBean agentMBeanProxy = jmxClient.getMBeanProxy();
            if (agentMBeanProxy != null)
            {
                String[] agentNames = agentMBeanProxy.getAgentNames();
                return Arrays.asList(agentNames);
            }
            else
            {
                LOG.debug("Unable to retrieve agentMBeanProxy");
            }
            LOG.warn(MSG_UNABLE_TO_RETRIEVE_NAMES);
            return Collections.emptyList();
        }
        catch (Exception exception)
        {
            LOG.warn(MSG_UNABLE_TO_RETRIEVE_NAMES);
            LOG.debug("An exception occurred trying to retrieve agent names", exception);
            return Collections.emptyList();
        }
    }

    public AgentCompletionCandidates()
    {
        super(getAgentNames());
    }
}
