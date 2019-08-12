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

import net.obvj.smart.conf.xml.XmlAgent;
import net.obvj.smart.conf.xml.XmlSmart;

/**
 * An object that maintains agent configuration data retrieved from the {@code agents.xml} file
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentConfiguration
{
    private static final Logger LOG = Logger.getLogger("smart-server");

    private static final String AGENTS_XML = "agents.xml";

    private static AgentConfiguration instance;

    private XmlSmart agents;

    private Map<String, XmlAgent> agentsByName = new HashMap<>();
    
    private AgentConfiguration()
    {
        agents = loadAgentsXmlFile(AGENTS_XML);
        loadAgentsMap();
    }

    protected static XmlSmart loadAgentsXmlFile(String fileName)
    {
        XmlSmart xmlAgents = null;
        LOG.log(Level.INFO, "Searching for agents file: {0}", fileName);
        try (final InputStream stream = AgentConfiguration.class.getClassLoader().getResourceAsStream(fileName))
        {
            if (stream == null)
            {
                throw new FileNotFoundException(String.format("Unable to find %s in class path", fileName));
            }

            LOG.log(Level.INFO, "{0} found", fileName);
            JAXBContext jaxb = JAXBContext.newInstance(XmlSmart.class);
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            unmarshaller.setSchema(loadSchema());
            xmlAgents = (XmlSmart) unmarshaller.unmarshal(stream);

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
        return sf.newSchema(AgentConfiguration.class.getClassLoader().getResource("agents.xsd"));
    }
    
    private void loadAgentsMap()
    {
        agents.getAgents().forEach(agent -> agentsByName.put(agent.getName(), agent));
    }

    public List<XmlAgent> getAgents()
    {
        return agents != null ? agents.getAgents() : Collections.emptyList();
    }
    
    public XmlAgent getAgentConfiguration(String name)
    {
        return agentsByName.get(name);
    }

    public static AgentConfiguration getInstance()
    {
        if (instance == null)
        {
            instance = new AgentConfiguration();
        }
        return instance;
    }

}
