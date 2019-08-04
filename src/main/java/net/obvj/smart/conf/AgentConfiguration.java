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

    private static final AgentConfiguration instance = new AgentConfiguration();

    private XmlSmart agents;

    private Map<String, XmlAgent> agentsByName = new HashMap<>();
    
    private AgentConfiguration()
    {
        loadAgentsXmlFile();
        loadAgentsMap();
    }

    private void loadAgentsXmlFile()
    {
        LOG.info("Searching for agents.xml file...");
        try (final InputStream stream = AgentConfiguration.class.getClassLoader().getResourceAsStream("agents.xml"))
        {
            JAXBContext jaxb = JAXBContext.newInstance(XmlSmart.class);

            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            unmarshaller.setSchema(loadSchema());
            agents = (XmlSmart) unmarshaller.unmarshal(stream);

            LOG.log(Level.INFO, "{0} agents found", agents.getAgents().size());
        }
        catch (NullPointerException e)
        {
            LOG.severe("Unable to find agents.xml file in the class path");
        }
        catch (UnmarshalException e)
        {
            LOG.log(Level.SEVERE, "Invalid agents.xml file", e);
        }
        catch (JAXBException | IOException e)
        {
            LOG.log(Level.SEVERE, "Unable to read agents.xml file", e);
        }
        catch (SAXException e)
        {
            LOG.log(Level.SEVERE, "Unable to parse agents.xsd schema file", e);
        }
    }

    private Schema loadSchema() throws SAXException
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
        return instance;
    }

}
