package net.obvj.smart.agents.api;

import org.junit.Test;

import net.obvj.smart.conf.xml.AgentConfiguration;

/**
 * Unit tests for the {@link DaemonAgent} class.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class DaemonAgentTest
{
    /**
     * Tests that a non-daemon agent will not be parsed by this class
     */
    @Test(expected = IllegalArgumentException.class)
    public void testParseNonDaemonAgent() throws ReflectiveOperationException
    {
        DaemonAgent.parseAgent(new AgentConfiguration.Builder("DummyAgent").type("timer")
                .agentClass("net.obvj.smart.agents.dummy.DummyDaemonAgent").build());
    }

}
