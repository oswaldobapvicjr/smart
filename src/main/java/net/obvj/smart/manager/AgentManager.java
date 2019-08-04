package net.obvj.smart.manager;

import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.agents.api.DaemonAgent;
import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.conf.xml.XmlAgent;

/**
 * A single object for agents maintenance
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public final class AgentManager
{
    private static final String MSG_PATTERN_INVALID_AGENT = "Invalid agent: %s";

    private static final AgentManager instance = new AgentManager();

    private Map<String, Agent> agents = new TreeMap<>();
    private final Logger logger = Logger.getLogger("smart-server");

    private AgentManager()
    {
    }

    public static AgentManager getInstance()
    {
        return instance;
    }

    /**
     * Registers a new agent for maintenance
     * 
     * @param agent the agent to be registered
     */
    public void addAgent(Agent agent)
    {
        String name = agent.getName();
        agents.put(name, agent);
    }

    /**
     * Removes an agent identified by the given name, if it is not started
     * 
     * @param name the identifier of the agent to be removed
     * @throws IllegalArgumentException if no agent with the given name was found
     * @throws IllegalStateException    if the requested agent is started
     */
    public void removeAgent(String name)
    {
        if (agents.containsKey(name))
        {
            Agent agent = agents.get(name);
            if (agent.isStarted())
            {
                throw new IllegalStateException("'" + name + "' is started. Please stop this agent first.");
            }
            agents.remove(name);
        }
        else
        {
            throw new IllegalArgumentException(String.format(MSG_PATTERN_INVALID_AGENT, name));
        }
    }

    /**
     * Creates a new instance of the agent identified by the given name
     * 
     * @param name the identifier of the agent to be reset
     * @return {@code true} if the operation succeeds, otherwise {@code false}
     * @throws IllegalArgumentException if no agent with the given name was found
     * @throws IllegalStateException    if the requested agent is not stopped
     */
    public boolean resetAgent(String name)
    {
        if (agents.containsKey(name))
        {
            Agent agent = agents.get(name);

            switch (agent.getState())
            {
            case SET:
                throw new IllegalStateException("'" + name + "' is already set. No action was taken.");
            case STARTED:
                throw new IllegalStateException("'" + name + "' is started. Please stop this agent first.");
            case RUNNING:
                throw new IllegalStateException("'" + name + "' task is currently under execution.");
            case STOPPED:
                try
                {
                    // Creating a new agent
                    XmlAgent agentConfig = AgentConfiguration.getInstance().getAgentConfiguration(name);
                    Agent newAgent = Agent.parseAgent(agentConfig);
                    addAgent(newAgent);
                    return true;
                }
                catch (Exception ex)
                {
                    logger.severe("Error reseting agent: " + ex.getClass().getName() + ": " + ex.getMessage());
                    return false;
                }
            default:
                throw new IllegalStateException(agent.getStatusString());
            }
        }
        else
        {
            throw new IllegalArgumentException(String.format(MSG_PATTERN_INVALID_AGENT, name));
        }
    }

    /**
     * Starts the agent identified by the given name
     * 
     * @param name the identifier of the agent to be started
     * @throws IllegalArgumentException if no agent with the given name was found
     * @throws IllegalStateException    if the requested agent is already started
     */
    public void startAgent(String name)
    {
        if (agents.containsKey(name))
        {
            agents.get(name).start();
        }
        else
        {
            throw new IllegalArgumentException(String.format(MSG_PATTERN_INVALID_AGENT, name));
        }
    }

    /**
     * Posts immediate execution of the agent identified by the given name
     * 
     * @param name the identifier of the agent to be run
     * @throws IllegalArgumentException      if no agent with the given name was found
     * @throws UnsupportedOperationException if the requested agent is a daemon agent
     */
    public void runNow(String name)
    {
        if (agents.containsKey(name))
        {
            Agent agent = agents.get(name);
            if (agent instanceof DaemonAgent)
            {
                throw new UnsupportedOperationException("Cannot run a daemon agent task manually");
            }
            agents.get(name).run();
        }
        else
        {
            throw new IllegalArgumentException(String.format(MSG_PATTERN_INVALID_AGENT, name));
        }
    }

    /**
     * Posts a graceful stop request for the agent identified by the given name
     * 
     * @param name the identifier of the agent to be stopped
     * @throws IllegalArgumentException if no agent with the given name was found
     * @throws TimeoutException         if the requested agent did not complete its normal
     *                                  execution after agent's cancellation timeout
     */
    public boolean stopAgent(String name) throws TimeoutException
    {
        if (agents.containsKey(name))
        {
            agents.get(name).stop();
            return true;
        }
        else
        {
            throw new IllegalArgumentException(String.format(MSG_PATTERN_INVALID_AGENT, name));
        }
    }

    public Collection<Agent> getAgents()
    {
        return agents.values();
    }

    public String[] getAgentNames()
    {
        return agents.keySet().toArray(new String[] {});
    }

    /**
     * @return a collection of agents metadata for interchange with client applications
     */
    public Collection<AgentDTO> getAgentDTOs()
    {
        List<AgentDTO> agentDTOs = new ArrayList<>(agents.size());
        agents.values().forEach(
                agent -> agentDTOs.add(new AgentDTO(agent.getName(), agent.getType(), agent.getState().toString())));
        return agentDTOs;
    }

    /**
     * Returns a flag indicating whether an agent is running or not
     * 
     * @param name the identifier of the agent to be reset
     * @return {@code true} if the agent is running, otherwise {@code false}
     * @throws IllegalArgumentException if no agent with the given name was found
     */
    public boolean isAgentRunning(String name)
    {
        if (agents.containsKey(name))
        {
            return agents.get(name).isRunning();
        }
        else
        {
            throw new IllegalArgumentException(String.format(MSG_PATTERN_INVALID_AGENT, name));
        }
    }

    /**
     * Returns a flag indicating whether an agent is started or not
     * 
     * @param name the identifier of the agent to be reset
     * @return {@code true} if the agent is started, otherwise {@code false}
     * @throws IllegalArgumentException if no agent with the given name was found
     */
    public boolean isAgentStarted(String name)
    {
        if (agents.containsKey(name))
        {
            return agents.get(name).isStarted();
        }
        else
        {
            throw new IllegalArgumentException(String.format(MSG_PATTERN_INVALID_AGENT, name));
        }
    }

    /**
     * Returns a string containing agent status information for reporting
     * 
     * @param name the identifier of the agent to be reported
     * @return a String containing agent status information and other metadata
     * @throws IllegalArgumentException if no agent with the given name was found
     */
    public String getAgentStatusStr(String name)
    {
        if (agents.containsKey(name))
        {
            return agents.get(name).getStatusString();
        }
        else
        {
            throw new IllegalArgumentException(String.format(MSG_PATTERN_INVALID_AGENT, name));
        }
    }

}
