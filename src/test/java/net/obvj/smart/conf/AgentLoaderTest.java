package net.obvj.smart.conf;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import net.obvj.smart.agents.test.valid.TestTimerAgent1;

@RunWith(MockitoJUnitRunner.class)
public class AgentLoaderTest
{
    private static final String TEST_TIMER_AGENT1_CLASS = TestTimerAgent1.class.getName();

    // ----------------------------------
    // Annotated agents
    // ----------------------------------
    // TestTimerAgent1, timer, net.obvj.smart.agents.test.valid.TestTimerAgent1, 29 seconds, hidden
    private static final AgentConfiguration AGENT_CONFIGURATION_TIMER_1 = AgentConfiguration
            .fromAnnotatedClass(TestTimerAgent1.class);

    // ----------------------------------
    // XML agents
    // ----------------------------------
    // XmlName, timer, net.obvj.smart.agents.test.valid.TestTimerAgent1, 30 seconds, not hidden
    private static final List<AgentConfiguration> XML_TIMER_AGEMT_1 = AgentsXml
            .loadAgentsXmlFile("testAgents/timerAgent1.xml").getAgents();

    @Mock
    private AgentsXml xmlAgents;
    @Mock
    private AnnotatedAgents annotatedAgents;

    private AgentLoader loader;

    @Test
    public void loadSameClassInBothAnnotationAndXml()
    {
        Mockito.when(xmlAgents.getAgents()).thenReturn(XML_TIMER_AGEMT_1);

        Mockito.when(annotatedAgents.getAgentsByClassName())
                .thenReturn(Collections.singletonMap(TEST_TIMER_AGENT1_CLASS, AGENT_CONFIGURATION_TIMER_1));

        loader = new AgentLoader(annotatedAgents, xmlAgents);
        
        // Since both are the same class, it must be merged
        assertEquals(1, loader.getAgents().size());
        
        AgentConfiguration config = loader.getAgentConfigurationByClass(TEST_TIMER_AGENT1_CLASS);
        // The XML configuration must have been chosen 
        assertEquals(config, XML_TIMER_AGEMT_1.get(0));
    }

}
