package net.obvj.smart.conf;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final List<String> INTERNAL_AGENTS_PACKAGES = Arrays.asList("net.obvj.smart.agents.internal");

    private static final Logger LOG = LoggerFactory.getLogger("smart-server");

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

        LOG.info("Scanning base packages: {}", searchPackages);
        Set<String> classNames = new HashSet<>();
        searchPackages.forEach(searchPackage -> classNames
                .addAll(AnnotationUtils.findClassesWithAnnotation(Agent.class, searchPackage)));

        LOG.info("{} agent candidate(s) found: {}", classNames.size(), classNames);
        return classNames;
    }

    /**
     * @return a List containing internal and user-defined package(s) to search for agents
     */
    private List<String> getSearchPackages()
    {
        List<String> searchPackages = new ArrayList<>(INTERNAL_AGENTS_PACKAGES);
        List<String> userSearchPackages = properties.getPropertiesListSplitBy(SmartProperties.AGENT_SEARCH_PACKAGES, ",");
        searchPackages.addAll(userSearchPackages);
        return searchPackages;
    }

    protected Class<?> toClass(String className)
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException exception)
        {
            throw new AgentConfigurationException(exception);
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
