package net.obvj.smart.manager;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.obvj.smart.agents.Agent;
import net.obvj.smart.agents.AgentFactory;
import net.obvj.smart.agents.dto.AgentDTO;
import net.obvj.smart.conf.AgentConfiguration;
import net.obvj.smart.conf.AgentLoader;
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
    private static final String SYSTEM_LINE_SEPARATOR = System.getProperty("line.separator");

    private static final Logger LOG = LoggerFactory.getLogger("smart-server");

    private AgentLoader agentLoader;

    private Map<String, Agent> agents = new TreeMap<>();

    @Autowired
    public AgentManager(AgentLoader agentLoader)
    {
        this.agentLoader = agentLoader;
        loadAgents();
    }

    /**
     * Loads agent candidates retrieved by the {@link AgentLoader}.
     */
    public void loadAgents()
    {
        LOG.info("Parsing agents...");

        Collection<AgentConfiguration> agentCandidates = agentLoader.getAgents();
        agentCandidates.stream().map(this::createAgent).filter(Optional::isPresent).map(Optional::get)
                .forEach(this::addAgent);

        LOG.info("{}/{} agent(s) loaded successfully: {}",
                agents.size(), agentCandidates.size(), agents.values());
    }

    /**
     * Creates an Agent for the given {@link AgentConfiguration}.
     *
     * @param agentConfiguration the {@link AgentConfiguration} to be parsed
     * @return an Optional which may contain a valid {@link Agent}, or
     *         {@link Optional#empty()} if unable to parse the given configuration
     */
    private Optional<Agent> createAgent(AgentConfiguration agentConfiguration)
    {
        try
        {
            return Optional.of(AgentFactory.create(agentConfiguration));
        }
        catch (Exception exception)
        {
            LOG.error("Error loading agent: {}", agentConfiguration.getName(), exception);
            return Optional.empty();
        }
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
        AgentConfiguration agentConfig = agentLoader
                .getAgentConfigurationByClass(agent.getConfiguration().getAgentClass());
        Agent newAgent = AgentFactory.create(agentConfig);
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
     */
    public void runNow(String name)
    {
        findAgentByName(name).run(true);
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

    /**
     * Returns all public (not-hidden) agent names.
     *
     * @return an array of public agent names
     */
    public String[] getPublicAgentNames()
    {
        return agents.entrySet().stream().filter(entry -> !entry.getValue().isHidden()).map(Map.Entry::getKey)
                .toArray(String[]::new);
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
        return getAgentStatusStr(name, true);
    }

    protected String getAgentStatusStr(String name, boolean prettyPrinting)
    {
        String statusString = findAgentByName(name).getStatusString();
        return prettyPrinting ? getPrettyPrintedJson(statusString) : statusString;
    }

    /**
     * @param string the JSON object representation to be converted
     * @return a JSON representation that fits in a page for pretty printing
     */
    private String getPrettyPrintedJson(String string)
    {
        JsonObject jsonObject = new Gson().fromJson(string, JsonObject.class);
        String prettyPrintedJson = new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject);
        return prettyPrintedJson.replace("\n", SYSTEM_LINE_SEPARATOR);
    }

}
