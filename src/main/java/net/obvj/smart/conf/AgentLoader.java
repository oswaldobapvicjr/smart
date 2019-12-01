package net.obvj.smart.conf;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        Map<String, AgentConfiguration> annotatedAgentsByClass = annotatedAgents.getAgentsByClassName();
        List<AgentConfiguration> xmlAgentsList = xmlAgents.getAgents();

        // First load the annotated agents
        Map<String, AgentConfiguration> overlayedAgentsByClass = new HashMap<>(annotatedAgentsByClass);

        // Then, start including the XML agents, if the same class is configured in both
        // annotation and XML, use the XML configuration
        xmlAgentsList.forEach(xmlAgent -> overlayedAgentsByClass.put(xmlAgent.getAgentClass(), xmlAgent));

        this.agentsByClass = overlayedAgentsByClass;
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