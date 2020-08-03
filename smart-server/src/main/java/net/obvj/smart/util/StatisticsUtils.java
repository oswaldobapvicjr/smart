package net.obvj.smart.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import org.springframework.util.CollectionUtils;

/**
 * Common methods for working with statistics.
 *
 * @author oswaldo.bapvic.jr
 * @since 2.0.0
 */
public class StatisticsUtils
{
    private StatisticsUtils()
    {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Computes and returns the average of all BigDecimals in a collection.
     *
     * @param collection the collection which average is to be calculated; can be null
     * @return the average of elements in the collection, represented as a BigDecimal; or
     *         {@code BigDecimal.ZERO}, if the collection is either null or empty
     */
    public static BigDecimal average(Collection<BigDecimal> collection)
    {
        if (CollectionUtils.isEmpty(collection))
        {
            return BigDecimal.ZERO;
        }
        BigDecimal sumOfElements = BigDecimal.ZERO;
        int countOfElements = 0;
        for (BigDecimal element : collection)
        {
            if (element != null)
            {
                sumOfElements = sumOfElements.add(element);
                countOfElements++;
            }
        }
        return countOfElements == 0 ? BigDecimal.ZERO
                : sumOfElements.divide(BigDecimal.valueOf(countOfElements), RoundingMode.HALF_UP);
    }
}
