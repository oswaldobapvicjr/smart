package net.obvj.smart.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

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

    public static ThreadInfo[] getAllSystemTheadsInfo()
    {
        ThreadMXBean tMXBean = ManagementFactory.getThreadMXBean();
        return tMXBean.getThreadInfo(tMXBean.getAllThreadIds());
    }

}
