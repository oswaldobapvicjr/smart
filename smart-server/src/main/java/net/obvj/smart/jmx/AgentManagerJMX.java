package net.obvj.smart.jmx;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

import net.obvj.smart.agents.dto.AgentDTO;
import net.obvj.smart.jmx.dto.ThreadDTO;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.ApplicationContextFacade;
import net.obvj.smart.util.DateUtils;
import net.obvj.smart.util.SystemUtils;

public class AgentManagerJMX implements AgentManagerJMXMBean
{
    private AgentManager agentManager = ApplicationContextFacade.getBean(AgentManager.class);
    
    public void startAgent(String name)
    {
        agentManager.startAgent(name);
    }

    public void resetAgent(String name) throws ReflectiveOperationException
    {
        agentManager.resetAgent(name);
    }

    public void runNow(String name)
    {
        agentManager.runNow(name);
    }

    public void stopAgent(String name) throws TimeoutException
    {
        agentManager.stopAgent(name);
    }

    public String[] getAgentNames()
    {
        return agentManager.getAgentNames();
    }

    public Collection<AgentDTO> getAgentDTOs()
    {
        return agentManager.getAgentDTOs();
    }

    public boolean isAgentRunning(String name)
    {
        return agentManager.isAgentRunning(name);
    }

    public boolean isAgentStarted(String name)
    {
        return agentManager.isAgentStarted(name);
    }

    public String getAgentStatusStr(String name)
    {
        return agentManager.getAgentStatusStr(name);
    }

    public String getServerDate()
    {
        return DateUtils.formattedCurrentDate();
    }

    public long getServerUptime()
    {
        return SystemUtils.getSystemUptime();
    }

    public Collection<ThreadDTO> getAllThreadsInfo()
    {
        return SystemUtils.getAllSystemTheadsDTOs();
    }
    
    public String getJavaVersion()
    {
        return SystemUtils.getJavaVersion();
    }

}
