package net.obvj.smart.manager;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.agents.api.DaemonAgent;

public final class AgentManager
{

    private static final String MSG_PATTERN_INVALID_AGENT = "Invalid agent: %s";

    private static final AgentManager instance = new AgentManager();
    
    private Map<String, Agent> agents = new TreeMap<>();
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private AgentManager()
    {
    }

    public static AgentManager getInstance()
    {
        return instance;
    }

    public void addAgent(Agent agent)
    {
        String name = agent.getName();
        if (!agents.containsKey(name))
        {
            agents.put(name, agent);
        }
    }

    public void removeAgent(String name)
    {
        if (agents.containsKey(name))
        {
            Agent agent = agents.get(name);
            if (agent.isStarted())
            {
                throw new IllegalStateException("'" + name + "' is started. Please stop this agent first.");
            }
            else
            {
                agents.remove(name);
            }
        }
        else
        {
            throw new IllegalArgumentException(String.format(MSG_PATTERN_INVALID_AGENT, name));
        }
    }

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
                    // Creating a new BaseAgent object
                    Agent newAgent = agent.getClass().getConstructor(String.class).newInstance(name);
                    // Removing old agent and adding the new one
                    removeAgent(name);
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
