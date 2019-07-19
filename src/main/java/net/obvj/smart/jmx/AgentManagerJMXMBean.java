package net.obvj.smart.jmx;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

import net.obvj.smart.agents.api.dto.AgentDTO;

public interface AgentManagerJMXMBean
{

    void startAgent(String name);

    boolean stopAgent(String name) throws TimeoutException;

    void runNow(String name);

    boolean resetAgent(String name);

    String[] getAgentNames();

    Collection<AgentDTO> getAgentsDTO();

    boolean isAgentRunning(String name);

    boolean isAgentStarted(String name);

    String getAgentStatusStr(String name);
    
    String getServerDate();

}
