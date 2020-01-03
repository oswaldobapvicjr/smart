package net.obvj.smart.conf.annotation;

import static org.junit.Assert.*;

import org.junit.Test;

public class TypeTest
{
    @Test
    public void toString_succeeds()
    {
        assertEquals("timer", Type.TIMER.toString());
    }

}
