package com.noleme.vault.service;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 24/05/2021
 */
public class StatefulService
{
    private String value;
    private int callCount;

    public StatefulService()
    {
        this(null);
    }

    public StatefulService(String value)
    {
        this.value = value;
    }

    public StatefulService setValue(String value)
    {
        this.value = value;
        this.callCount++;
        return this;
    }

    public String getValue()
    {
        return this.value;
    }

    public int getCallCount()
    {
        return this.callCount;
    }
}
