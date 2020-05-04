package net.obvj.smart.conf;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.obvj.performetrics.Counter;
import net.obvj.performetrics.Counter.Type;
import net.obvj.performetrics.Stopwatch;

/**
 * A component that loads agent objects by coordinating configuration data from
 * annotations and XML file.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Component
public class AgentLoader
{
    private static final Logger LOG = LoggerFactory.getLogger("smart-server");

    private AnnotatedAgents annotatedAgents;
    private AgentsXml xmlAgents;

    private Map<String, AgentConfiguration> agentsByClass = new HashMap<>();

    @Autowired
    public AgentLoader(AnnotatedAgents annotatedAgents, AgentsXml xmlAgents)
    {
        this.annotatedAgents = annotatedAgents;
        this.xmlAgents = xmlAgents;
        loadAgents();
    }

    public void loadAgents()
    {
        Stopwatch stopwatch = Stopwatch.createStarted(Counter.Type.WALL_CLOCK_TIME);

        Map<String, AgentConfiguration> annotatedAgentsByClass = annotatedAgents.getAgentsByClassName();
        List<AgentConfiguration> xmlAgentsList = xmlAgents.getAgents();

        // First load the annotated agents
        Map<String, AgentConfiguration> overlayedAgentsByClass = new HashMap<>(annotatedAgentsByClass);

        // Then, start including the XML agents, if the same class is configured in both
        // annotation and XML, use the XML configuration
        xmlAgentsList.forEach(xmlAgent -> overlayedAgentsByClass.put(xmlAgent.getAgentClass(), xmlAgent));

        this.agentsByClass = overlayedAgentsByClass;

        long elapsedTime = stopwatch.getCounter(Type.WALL_CLOCK_TIME).elapsedTime();
        LOG.info("{} agents(s) loaded in {} nanoseconds", agentsByClass.size(), elapsedTime);
    }

    /**
     * @return a collection of {@link AgentConfiguration} objects
     */
    public Collection<AgentConfiguration> getAgents()
    {
        return agentsByClass.values();
    }

    public AgentConfiguration getAgentConfigurationByClass(String className)
    {
        return agentsByClass.get(className);
    }

}
