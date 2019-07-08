package net.obvj.smart.agents.dummy;

import net.obvj.smart.agents.api.TimerAgent;

public class DummyAgent extends TimerAgent
{

    public DummyAgent(String name)
    {
        super(name);
    }

    @Override
    protected void runTask()
    {
        for (int i = 9; i >= 0; i--)
        {
            try
            {
                System.out.println(i);
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getStopTimeoutSeconds()
    {
        return 5;
    }

}
