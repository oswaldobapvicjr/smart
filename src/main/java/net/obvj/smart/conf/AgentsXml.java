package net.obvj.smart.conf;

import java.io.FileNotFoundException;
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

import org.xml.sax.SAXException;

import net.obvj.smart.conf.xml.AgentConfiguration;
import net.obvj.smart.conf.xml.SmartConfiguration;

/**
 * An object that maintains agent configuration data retrieved from the {@code agents.xml} file
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentsXml
{
    private static final Logger LOG = Logger.getLogger("smart-server");

    private static final String AGENTS_XML = "agents.xml";

    private static final AgentsXml INSTANCE = new AgentsXml();

    private SmartConfiguration agents;

    private Map<String, AgentConfiguration> agentsByName = new HashMap<>();
    
    private AgentsXml()
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
        LOG.log(Level.INFO, "Searching for agents file: {0}", fileName);
        try (final InputStream stream = AgentsXml.class.getClassLoader().getResourceAsStream(fileName))
        {
            if (stream == null)
            {
                throw new FileNotFoundException(String.format("Unable to find %s in class path", fileName));
            }

            LOG.log(Level.INFO, "{0} found", fileName);
            JAXBContext jaxb = JAXBContext.newInstance(SmartConfiguration.class);
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            unmarshaller.setSchema(loadSchema());
            xmlAgents = (SmartConfiguration) unmarshaller.unmarshal(stream);

            LOG.log(Level.INFO, "{0} agent(s) found", xmlAgents.getAgents().size());
        }
        catch (UnmarshalException e)
        {
            throw new AgentConfigurationException("Invalid agents file", e);
        }
        catch (JAXBException | IOException e)
        {
            throw new AgentConfigurationException(e);
        }
        catch (SAXException e)
        {
            throw new AgentConfigurationException("Unable to parse agents.xsd schema file", e);
        }
        return xmlAgents;
    }

    private static Schema loadSchema() throws SAXException
    {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return sf.newSchema(AgentsXml.class.getClassLoader().getResource("agents.xsd"));
    }
    
    private void loadAgentsMap()
    {
        agents.getAgents().forEach(agent -> agentsByName.put(agent.getName(), agent));
    }

    public List<AgentConfiguration> getAgents()
    {
        return agents != null ? agents.getAgents() : Collections.emptyList();
    }
    
    public AgentConfiguration getAgentConfiguration(String name)
    {
        return agentsByName.get(name);
    }

    public static AgentsXml getInstance()
    {
        return INSTANCE;
    }

}
