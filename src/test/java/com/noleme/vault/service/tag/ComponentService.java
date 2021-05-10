package com.noleme.vault.service.tag;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 06/05/2021
 */
public class ComponentService
{
    private final String value;

    public ComponentService(String value)
    {
        this.value = value;
    }

    public String value()
    {
        return this.value;
    }
}
