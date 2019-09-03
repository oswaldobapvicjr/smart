package net.obvj.smart.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import net.obvj.smart.jmx.dto.ThreadDTO;

/**
 * Utility class with facilities for gathering of system information
 * 
 * @author oswaldo.bapvic.jr
 * @since 1.0
 */
public class SystemUtil
{
    private SystemUtil()
    {
        throw new IllegalStateException("Utility class");
    }

    public static long getSystemUptime()
    {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    private static ThreadInfo[] getAllSystemTheadsInfo()
    {
        ThreadMXBean tMXBean = ManagementFactory.getThreadMXBean();
        return tMXBean.getThreadInfo(tMXBean.getAllThreadIds());
    }

    public static Collection<ThreadDTO> getAllSystemTheadsDTOs()
    {
        return getSystemTheadsDTOs(getAllSystemTheadsInfo());
    }
    
    protected static Collection<ThreadDTO> getSystemTheadsDTOs(ThreadInfo[] threadsInfo)
    {
        return Arrays
                .stream(threadsInfo).map(threadInfo -> new ThreadDTO(threadInfo.getThreadId(),
                        threadInfo.getThreadName(), threadInfo.getThreadState().toString()))
                .collect(Collectors.toList());
    }

}
