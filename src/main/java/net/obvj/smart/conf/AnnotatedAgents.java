package net.obvj.smart.conf;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.obvj.smart.conf.annotation.Agent;
import net.obvj.smart.conf.properties.SmartProperties;
import net.obvj.smart.util.AnnotationUtils;

/**
 * A component that holds annotated {@link Agent} objects, loaded from configured base
 * package(s) in class path.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Component
public class AnnotatedAgents
{
    private final Logger log = Logger.getLogger("smart-server"); 
    
    private SmartProperties properties;

    private Map<String, AgentConfiguration> agentsByClass = new HashMap<>();

    @Autowired
    public AnnotatedAgents(SmartProperties properties)
    {
        this.properties = properties;
        Set<String> classNames = findAnnotatedAgentClasses();
        Set<Class<?>> classes = classNames.stream().map(this::toClass).collect(Collectors.toSet());
        classes.forEach(clazz -> agentsByClass.put(clazz.getName(), AgentConfiguration.fromAnnotatedClass(clazz)));
    }

    /**
     * @returns a list of candidate class names found in class path with the {@code @Agent}
     *          annotation
     */
    protected Set<String> findAnnotatedAgentClasses()
    {
        List<String> searchPackages = getSearchPackages();

        log.log(Level.INFO, "Scanning base packages: {0}", searchPackages);
        Set<String> classNames = new HashSet<>();
        searchPackages.forEach(searchPackage -> classNames
                .addAll(AnnotationUtils.findClassesWithAnnotation(Agent.class, searchPackage)));
        
        log.log(Level.INFO, "{0} agent candidate(s) found: {1}", new Object[] { classNames.size(), classNames });
        return classNames;
    }

    /**
     * @return the search package(s) configured in properties
     */
    private List<String> getSearchPackages()
    {
        return properties.getPropertiesListSplitBy(SmartProperties.AGENT_SEARCH_PACKAGES, ",");
    }

    protected Class<?> toClass(String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e)
        {
            throw new AgentConfigurationException(e);
        }
    }

    /**
     * @return a {@link Map} of {@link AgentConfiguration} by annotated class name
     */
    public Map<String, AgentConfiguration> getAgentsByClassName()
    {
        return agentsByClass;
    }

}
