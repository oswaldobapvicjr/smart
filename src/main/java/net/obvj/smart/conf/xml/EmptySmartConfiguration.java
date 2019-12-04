package net.obvj.smart.conf.xml;

import java.util.Collections;
import java.util.List;

import net.obvj.smart.conf.AgentConfiguration;

/**
 * A null object for the {@link SmartConfiguration} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public final class EmptySmartConfiguration extends SmartConfiguration
{
    @Override
    public final List<AgentConfiguration> getAgents()
    {
        return Collections.emptyList();
    }

}
