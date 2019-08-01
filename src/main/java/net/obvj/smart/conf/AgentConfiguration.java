package net.obvj.smart.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.obvj.smart.conf.xml.XmlAgent;
import net.obvj.smart.conf.xml.XmlSmart;

public class AgentConfiguration
{
    private static final Logger LOG = Logger.getLogger("smart-server");

    private static final AgentConfiguration instance = new AgentConfiguration();

    private XmlSmart agents;

    private AgentConfiguration()
    {
        loadAgentsXmlFile();
    }

    private void loadAgentsXmlFile()
    {
        LOG.info("Searching for agents.xml file...");
        try (final InputStream stream = SmartProperties.class.getClassLoader().getResourceAsStream("agents.xml"))
        {
            JAXBContext jaxb = JAXBContext.newInstance(XmlSmart.class);

            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            agents = (XmlSmart) unmarshaller.unmarshal(stream);

            LOG.log(Level.INFO, "{0} agents found", agents.getAgents().size());
        }
        catch (NullPointerException e)
        {
            LOG.severe("Unable to find agents.xml file in the class path");
        }
        catch (JAXBException | IOException e)
        {
            LOG.log(Level.SEVERE, "Unable to read agents.xml file", e);
        }
    }

    public List<XmlAgent> getAgents()
    {
        return agents != null ? agents.getAgents() : Collections.emptyList();
    }

    public static AgentConfiguration getInstance()
    {
        return instance;
    }

}
