package net.obvj.smart.util;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * Unit tests for the {@link StatisticsUtils}.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class StatisticsUtilsTest
{
    private static final List<BigDecimal> LIST1 = Arrays.asList(BigDecimal.valueOf(0.5), BigDecimal.valueOf(0.7));
    private static final List<BigDecimal> LIST_CONTAINING_NULL = Arrays.asList(BigDecimal.valueOf(0.345), (BigDecimal) null);
    private static final List<BigDecimal> LIST_CONTAINING_ONLY_NULLS = Arrays.asList((BigDecimal) null, (BigDecimal) null);

    @Test
    public void testNoInstancesAllowed()
    {
        assertThat(StatisticsUtils.class, instantiationNotAllowed());
    }

    @Test
    public void average_validList_computesAccordingly()
    {
        assertThat(StatisticsUtils.average(LIST1).doubleValue(), is(equalTo(0.6)));
    }

    @Test
    public void average_validListWithOneNull_computesAccordingly()
    {
        assertThat(StatisticsUtils.average(LIST_CONTAINING_NULL).doubleValue(), is(equalTo(0.345)));
    }

    @Test
    public void average_emptyList_zero()
    {
        assertThat(StatisticsUtils.average(Collections.emptyList()).doubleValue(), is(equalTo(0.0)));
    }

    @Test
    public void average_null_zero()
    {
        assertThat(StatisticsUtils.average(null).doubleValue(), is(equalTo(0.0)));
    }

    @Test
    public void average_listContainingNulls_zero()
    {
        assertThat(StatisticsUtils.average(LIST_CONTAINING_ONLY_NULLS).doubleValue(), is(equalTo(0.0)));
    }

}
