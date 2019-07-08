package net.obvj.smart.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Utility class with facilities for gathering of system information
 */
public class SystemUtil
{

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
