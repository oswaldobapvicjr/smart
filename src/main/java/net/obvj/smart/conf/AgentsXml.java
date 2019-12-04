package net.obvj.smart.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import net.obvj.smart.conf.xml.EmptySmartConfiguration;
import net.obvj.smart.conf.xml.SmartConfiguration;
import net.obvj.smart.util.Exceptions;

/**
 * An object that maintains agent configuration data retrieved from the {@code agents.xml} file
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@Component
public class AgentsXml
{
    private static final Logger LOG = Logger.getLogger("smart-server");

    private static final String AGENTS_XML = "agents.xml";
    private static final String AGENTS_XSD = "agents.xsd";

    private SmartConfiguration agents;

    private Map<String, AgentConfiguration> agentsByName = new HashMap<>();
    
    public AgentsXml()
    {
        this(AGENTS_XML);
    }
    
    protected AgentsXml(String fileName)
    {
        agents = loadAgentsXmlFile(fileName);
        loadAgentsMap();
    }

    protected static SmartConfiguration loadAgentsXmlFile(String fileName)
    {
        SmartConfiguration xmlAgents = null;
        try (final InputStream stream = AgentsXml.class.getClassLoader().getResourceAsStream(fileName))
        {
            if (stream == null)
            {
                LOG.log(Level.WARNING, "\"{0}\" not found in the class path", fileName);
                return new EmptySmartConfiguration();
            }

            LOG.log(Level.INFO, "{0} found", fileName);
            JAXBContext jaxb = JAXBContext.newInstance(SmartConfiguration.class);
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            unmarshaller.setSchema(loadSchema());
            xmlAgents = (SmartConfiguration) unmarshaller.unmarshal(stream);

            LOG.log(Level.INFO, "{0} agent candidate(s) found: {1}",
                    new Object[] { xmlAgents.getAgents().size(), xmlAgents.getAgents() });
        }
        catch (UnmarshalException e)
        {
            throw new AgentConfigurationException("Invalid agents file", e);
        }
        catch (JAXBException | IOException e)
        {
            throw new AgentConfigurationException(e);
        }
        return xmlAgents;
    }

    private static Schema loadSchema()
    {
        return loadSchemaFile(AGENTS_XSD);
    }
    
    protected static Schema loadSchemaFile(String fileName)
    {
        try
        {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            return sf.newSchema(AgentsXml.class.getClassLoader().getResource(fileName));
        }
        catch (SAXException e)
        {
            throw Exceptions.agentConfiguration(e, "Unable to parse schema file: %s", fileName);
        }
    }
    
    private void loadAgentsMap()
    {
        agents.getAgents().forEach(this::registerAgentConfiguration);
    }

    private void registerAgentConfiguration(AgentConfiguration agent)
    {
        agentsByName.put(agent.getName(), agent);
    }

    public List<AgentConfiguration> getAgents()
    {
        return agents != null ? agents.getAgents() : Collections.emptyList();
    }
    
    public AgentConfiguration getAgentConfiguration(String name)
    {
        return agentsByName.get(name);
    }

}
