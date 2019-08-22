package net.obvj.smart.jmx;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

import net.obvj.smart.agents.api.dto.AgentDTO;
import net.obvj.smart.jmx.dto.ThreadDTO;
import net.obvj.smart.manager.AgentManager;
import net.obvj.smart.util.DateUtil;
import net.obvj.smart.util.SystemUtil;

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

    public String[] getAgentNames()
    {
        return AgentManager.getInstance().getAgentNames();
    }

    public Collection<AgentDTO> getAgentDTOs()
    {
        return AgentManager.getInstance().getAgentDTOs();
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

    public String getServerDate()
    {
        return DateUtil.now();
    }

    public long getServerUptime()
    {
        return SystemUtil.getSystemUptime();
    }

    public Collection<ThreadDTO> getAllThreadsInfo()
    {
        return SystemUtil.getAllSystemTheadsDTOs();
    }

}
