package net.obvj.smart.jmx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeoutException;

import net.obvj.smart.agents.api.Agent;
import net.obvj.smart.manager.AgentManager;

public class AgentManagerJMX implements AgentManagerJMXMBean
{

    public void startAgent(String name)
    {
        AgentManager.getInstance().startAgent(name);
    }

    public boolean resetAgent(String name)
    {
        return AgentManager.getInstance().resetAgent(name);
    }

    public void runNow(String name)
    {
        AgentManager.getInstance().runNow(name);
    }

    public boolean stopAgent(String name) throws TimeoutException
    {
        return AgentManager.getInstance().stopAgent(name);
    }

    public Collection<String> getAgents()
    {
        List<String> agentsList = new ArrayList<>();
        for (Agent agent : AgentManager.getInstance().getAgents())
        {
            agentsList.add(agent.getName());
        }
        return agentsList;
    }

    public boolean isAgentRunning(String name)
    {
        return AgentManager.getInstance().isAgentRunning(name);
    }

    public boolean isAgentStarted(String name)
    {
        return AgentManager.getInstance().isAgentStarted(name);
    }

    public String getAgentStatusStr(String name)
    {
        return AgentManager.getInstance().getAgentStatusStr(name);
    }

}
