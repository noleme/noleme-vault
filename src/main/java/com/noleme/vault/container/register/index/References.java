package com.noleme.vault.container.register.index;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public class References extends Index<Reference>
{
    @Override
    public References set(String name, Reference reference)
    {
        return (References) super.set(name, reference);
    }

    @Override
    public References remove(String name)
    {
        return (References) super.remove(name);
    }
}
