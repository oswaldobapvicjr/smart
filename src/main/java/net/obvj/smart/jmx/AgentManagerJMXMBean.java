package net.obvj.smart.jmx;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

public interface AgentManagerJMXMBean
{

    void startAgent(String name);

    boolean stopAgent(String name) throws TimeoutException;

    void runNow(String name);

    boolean resetAgent(String name);

    Collection<String> getAgents();

    boolean isAgentRunning(String name);

    boolean isAgentStarted(String name);

    String getAgentStatusStr(String name);

}
