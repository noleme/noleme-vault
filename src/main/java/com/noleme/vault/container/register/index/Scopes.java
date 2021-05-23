package com.noleme.vault.container.register.index;

import com.noleme.vault.container.register.ScopedDefinitions;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public class Scopes extends Index<ScopedDefinitions>
{
    @Override
    public Scopes set(String name, ScopedDefinitions scope)
    {
        return (Scopes) super.set(name, scope);
    }

    @Override
    public Scopes remove(String name)
    {
        return (Scopes) super.remove(name);
    }
}