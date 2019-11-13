package net.obvj.smart.main;

import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.*;

import org.junit.Test;

/**
 * Unit tests for operations inside {@link SmartServerSupport}, with no mocks.
 * 
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class SmartServerSupportNoMocksTest
{
    private static final List<String> ALL_METHODS = Arrays.asList("startAgent", "isAgentRunning", "runNow", "stopAgent",
            "resetAgent", "isAgentStarted", "getAgentStatusStr");

    // Test subject
    private SmartServerSupport support = new SmartServerSupport();

    @Test
    public void testRegisteredManagedBean() throws Exception
    {
        support.registerManagedBean();
        ObjectName name = new ObjectName(support.jmxAgentManagerObjectName);

        assertTrue("Manageg bean not registered", ManagementFactory.getPlatformMBeanServer().isRegistered(name));

        MBeanOperationInfo[] operations = ManagementFactory.getPlatformMBeanServer().getMBeanInfo(name).getOperations();
        List<String> names = Arrays.stream(operations).map(MBeanOperationInfo::getName).collect(Collectors.toList());

        assertTrue("Not all expected methods registered", names.containsAll(ALL_METHODS));
    }

}
