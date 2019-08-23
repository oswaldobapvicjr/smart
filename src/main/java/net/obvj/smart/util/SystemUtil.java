package net.obvj.smart.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
        RuntimeMXBean rMXBean = ManagementFactory.getRuntimeMXBean();
        return rMXBean.getUptime();
    }
    private static ThreadInfo[] getAllSystemTheadsInfo()
    {
        ThreadMXBean tMXBean = ManagementFactory.getThreadMXBean();
        return tMXBean.getThreadInfo(tMXBean.getAllThreadIds());
    }

    public static Collection<ThreadDTO> getAllSystemTheadsDTOs()
    {
        ThreadInfo[] allThreadsInfo = getAllSystemTheadsInfo();
        List<ThreadDTO> dtos = new ArrayList<>(allThreadsInfo.length);
        Arrays.stream(allThreadsInfo).forEach(threadInfo -> dtos
                .add(new ThreadDTO(threadInfo.getThreadId(), threadInfo.getThreadName(), threadInfo.getThreadState().toString())));
        return dtos;
    }

}
