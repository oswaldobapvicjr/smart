package net.obvj.smart.jmx.client;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.obvj.smart.conf.SmartProperties;
import net.obvj.smart.util.ApplicationContextFacade;

/**
 * Unit tests for operations inside {@link AgentManagerJMXClient}.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ ApplicationContextFacade.class })
public class AgentManagerJMXClientTest
{
    private static final String JMX_OBJECT_DOMAIN = "net.obvj.smart.jmx";
    private static final String JMX_OBJECT_TYPE = "AgentManagerJMX";
    private static final String JMX_OBJECT_CANONICAL_NAME = "net.obvj.smart.jmx:type=AgentManagerJMX";

    @Mock
    private SmartProperties properties;

    @Before
    public void setup()
    {
        mockStatic(ApplicationContextFacade.class);
        when(ApplicationContextFacade.getBean(SmartProperties.class)).thenReturn(properties);
    }

    @Test
    public void testGetJMXObjectName() throws MalformedObjectNameException
    {
        when(properties.getProperty(SmartProperties.JMX_AGENT_MANAGER_OBJECT_NAME))
                .thenReturn(JMX_OBJECT_CANONICAL_NAME);

        ObjectName name = AgentManagerJMXClient.getAgentManagerJMXBeanObjectName();

        assertEquals(JMX_OBJECT_CANONICAL_NAME, name.getCanonicalName());
        assertEquals(JMX_OBJECT_DOMAIN, name.getDomain());
        assertEquals(JMX_OBJECT_TYPE, name.getKeyProperty("type"));
    }

    @Test
    public void testGetJMXRemotePort()
    {
        when(properties.getIntProperty(SmartProperties.JMX_REMOTE_PORT)).thenReturn(1234);
        assertEquals(1234, AgentManagerJMXClient.getJMXRemotePort());
    }

}
