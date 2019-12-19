package net.obvj.smart.jmx;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

import net.obvj.smart.agents.dto.AgentDTO;
import net.obvj.smart.jmx.dto.ThreadDTO;

public interface AgentManagerJMXMBean
{

    void startAgent(String name);

    void stopAgent(String name) throws TimeoutException;

    void runNow(String name);

    void resetAgent(String name) throws ReflectiveOperationException;

    String[] getAgentNames();

    Collection<AgentDTO> getAgentDTOs();

    boolean isAgentRunning(String name);

    boolean isAgentStarted(String name);

    String getAgentStatusStr(String name);
    
    String getServerDate();
    
    long getServerUptime();
    
    Collection<ThreadDTO> getAllThreadsInfo();
    
    String getJavaVersion();
}
