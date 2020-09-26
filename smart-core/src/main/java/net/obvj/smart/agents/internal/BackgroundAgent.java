package net.obvj.smart.agents.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private Logger log = LoggerFactory.getLogger(BackgroundAgent.class);

    @AgentTask
    public void keepAlive()
    {
        log.debug("[BackgroundAgent] Keep alive");
    }

}
