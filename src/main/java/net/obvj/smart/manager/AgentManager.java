package net.obvj.smart.manager;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.agents.api.DaemonAgent;
import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.conf.AgentsXml;
import net.obvj.smart.util.Exceptions;

/**
 * A component that provides methods for Agents maintenance
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
@Component
public class AgentManager
{
    private static final String MSG_INVALID_AGENT = "Invalid agent: %s";
    private static final String MSG_AGENT_STARTED_PLEASE_STOP_FIRST = "'%s' is started. Please stop the agent before this operation.";

    @Autowired
    private AgentsXml agentsXml;
    
    private Map<String, Agent> agents = new TreeMap<>();

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
     * @param name the agent to be found
     * @return the Agent associated with the given name
     * @throws IllegalArgumentException if no agent with the given name was found
     * @since 2.0
     */
    public Agent findAgentByName(String name)
    {
        if (agents.containsKey(name))
        {
            return agents.get(name);
        }
        throw Exceptions.illegalArgument(MSG_INVALID_AGENT, name);
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
        Agent agent = findAgentByName(name);
        if (agent.isStarted() || agent.isRunning())
        {
            throw Exceptions.illegalState(MSG_AGENT_STARTED_PLEASE_STOP_FIRST, name);
        }
        agents.remove(name);
    }

    /**
     * Creates a new instance of the agent identified by the given name
     * 
     * @param name the identifier of the agent to be reset
     * @return {@code true} if the operation succeeds, otherwise {@code false}
     * @throws ReflectiveOperationException if unable to parse a new agent object
     * @throws IllegalArgumentException     if no agent with the given name was found
     * @throws IllegalStateException        if the agent is either started or running
     */
    public void resetAgent(String name) throws ReflectiveOperationException
    {
        Agent agent = findAgentByName(name);
        if (agent.isStarted() || agent.isRunning())
        {
            throw Exceptions.illegalState(MSG_AGENT_STARTED_PLEASE_STOP_FIRST, name);
        }
        AgentConfiguration agentConfig = agentsXml.getAgentConfiguration(name);
        Agent newAgent = Agent.parseAgent(agentConfig);
        addAgent(newAgent);
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
        findAgentByName(name).start();
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
        Agent agent = findAgentByName(name);
        if (agent instanceof DaemonAgent)
        {
            throw new UnsupportedOperationException("Cannot run a daemon agent task manually");
        }
        agent.run(true);
    }

    /**
     * Posts a graceful stop request for the agent identified by the given name
     * 
     * @param name the identifier of the agent to be stopped
     * @throws IllegalArgumentException if no agent with the given name was found
     * @throws TimeoutException         if the requested agent did not complete its normal
     *                                  execution after agent's cancellation timeout
     */
    public void stopAgent(String name) throws TimeoutException
    {
        findAgentByName(name).stop();
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
     * @since 2.0
     */
    public Collection<AgentDTO> getAgentDTOs()
    {
        return agents.values().stream().map(this::toAgentDTO).collect(Collectors.toList());
    }

    /**
     * @return an {@link AgentDTO} from the given {@link Agent}
     * @since 2.0
     */
    protected AgentDTO toAgentDTO(Agent agent)
    {
        return new AgentDTO(agent.getName(), agent.getType(), agent.getState().toString(),
                agent.getConfiguration().isHidden());
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
        return findAgentByName(name).isRunning();
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
        return findAgentByName(name).isStarted();
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
        return findAgentByName(name).getStatusString();
    }

}
