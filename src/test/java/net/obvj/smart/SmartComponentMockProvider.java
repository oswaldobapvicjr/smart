package net.obvj.smart;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.obvj.smart.conf.AgentsXml;

@Configuration
public class SmartComponentMockProvider
{
    @Bean
    public AgentsXml agentsXml() {
        return Mockito.mock(AgentsXml.class);
    }
}
