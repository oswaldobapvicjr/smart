package net.obvj.smart.conf.xml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EmptySmartConfigurationTest
{

    @Test
    public void test()
    {
        assertEquals(0, new EmptySmartConfiguration().getAgents().size());
    }

}
