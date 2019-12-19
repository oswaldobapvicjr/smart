package net.obvj.smart.agents.internal;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.annotation.AgentTask;
import net.obvj.smart.conf.annotation.Type;

/**
 * An internal agent with the purpose to keep S.M.A.R.T. runtime service alive when there
 * is no other agent started in the platform.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Agent(type = Type.TIMER, hidden = true)
public class BackgroundAgent
{
    private Logger log = Logger.getLogger("smart-server");

    @AgentTask
    public void keepAlive()
    {
        log.log(Level.INFO, "[BackgroundAgent] Keep alive");
    }

}
