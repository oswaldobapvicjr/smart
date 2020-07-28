package net.obvj.smart.util;

import static net.obvj.junit.utils.matchers.InstantiationNotAllowedMatcher.instantiationNotAllowed;
import static net.obvj.junit.utils.matchers.StringMatcher.containsAll;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import org.junit.Test;

/**
 * Unit tests for the {@link ConsoleUtils} class.
 *
 * @author oswaldo.bapvic.jr
 */
public class ConsoleUtilsTest
{

    @Test
    public void testNoInstancesAllowed()
    {
        assertThat(ConsoleUtils.class, instantiationNotAllowed());
    }

    @Test
    public void testReadFileFromClasspathReturnsContent()
    {
        assertThat(ConsoleUtils.readCustomHeaderLines().toString(), containsAll("S.M.A.R.T. Console"));
    }

    @Test
    public void testReadFileListWithFileNotFound()
    {
        assertThat(ConsoleUtils.readFileLines("invalidFile.if"), is(equalTo(Collections.emptyList())));
    }

}
