package net.obvj.smart.agents.dummy;

import net.obvj.smart.agents.api.TimerAgent;

public class PMReporter extends TimerAgent
{

    public PMReporter(String name)
    {
        super(name);
    }

    @Override
    protected void runTask()
    {
        Runtime rt = Runtime.getRuntime();
        System.out.println("Loading statistics...");
        try
        {
            Thread.sleep(1000);
            System.out.println("AVAILABLE_PROCESSORS=" + rt.availableProcessors());
            Thread.sleep(1000);
            System.out.println("TOTAL_MEMORY=" + rt.totalMemory());
            Thread.sleep(1000);
            System.out.println("FREE_MEMORY=" + rt.freeMemory());
            Thread.sleep(1000);
            System.out.println("MAX_MEMORY=" + rt.maxMemory());

        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getStopTimeoutSeconds()
    {
        return 5;
    }

}
