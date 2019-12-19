package net.obvj.smart.agents.api.dto;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import net.obvj.smart.agents.dto.AgentDTO;

/**
 * Unit tests for the {@link AgentDTO}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0
 */
public class AgentDTOTest
{
    private static final AgentDTO AGENT1_1 = new AgentDTO("name1", "timer", "SET", true);
    private static final AgentDTO AGENT1_2 = new AgentDTO("name1", "timer", "SET", true);

    private static final AgentDTO AGENT2 = new AgentDTO("name2", "timer", "STARTED", false);

    @Test
    public void equals_sameFields_true()
    {
        assertThat(AGENT1_1.equals(AGENT1_2), is(true));
    }

    @Test
    public void equals_differentField_false()
    {
        assertThat(AGENT1_1.equals(AGENT2), is(false));
    }

    @Test
    public void equals_sameObject_true()
    {
        assertThat(AGENT1_1.equals(AGENT1_1), is(true));
    }

    @Test
    public void equals_null_false()
    {
        assertThat(AGENT1_1.equals(null), is(false));
    }

    @Test
    public void hashCode_addEqualObjectInASet_overwritesExistingOne()
    {
        Set<AgentDTO> agents = new HashSet<>();
        agents.add(AGENT1_1);
        agents.add(AGENT1_2);
        assertThat(agents.size(), is(1));
    }

    @Test
    public void getters_succeed()
    {
        assertThat(AGENT1_1.getName(), is("name1"));
        assertThat(AGENT1_1.getType(), is("timer"));
        assertThat(AGENT1_1.getState(), is("SET"));
        assertThat(AGENT1_1.isHidden(), is(true));
    }

}
