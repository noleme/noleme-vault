package com.noleme.vault.service;

import java.util.Collection;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 01/05/2021
 */
public class ListProvider <T> implements ValueProvider<Collection<T>>
{
    private final Collection<T> values;

    public ListProvider(Collection<T> values)
    {
        this.values = values;
    }

    @Override
    public Collection<T> provide()
    {
        return this.values;
    }
}
