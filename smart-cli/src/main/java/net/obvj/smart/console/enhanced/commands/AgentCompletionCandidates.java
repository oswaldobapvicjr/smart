package net.obvj.smart.console.enhanced.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private static final long serialVersionUID = 3919528430528865666L;

    private static final Logger LOG = Logger.getLogger("smart-cli");

    public static List<String> getAgentNames()
    {
        try
        {
            return Arrays.asList(ClientApplicationContextFacade.getBean(AgentManagerJMXClient.class).getMBeanProxy().getAgentNames());
        }
        catch (Exception e)
        {
            LOG.log(Level.WARNING, "Unable to retrieve available agent names");
            return Collections.emptyList();
        }
    }

    public AgentCompletionCandidates()
    {
        super(getAgentNames());
    }
}
