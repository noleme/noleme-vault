package com.noleme.vault.container.register.index;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public class Variables extends Index<Object>
{
    @Override
    public Variables set(String name, Object value)
    {
        return (Variables) super.set(name, value);
    }

    @Override
    public Variables remove(String name)
    {
        return (Variables) super.remove(name);
    }
}