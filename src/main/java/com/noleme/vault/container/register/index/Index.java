package com.noleme.vault.container.register.index;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public abstract class Index<T>
{
    private final Map<String, T> dictionary;

    Index()
    {
        this.dictionary = new HashMap<>();
    }

    public boolean has(String name)
    {
        return this.dictionary.containsKey(name);
    }

    public T get(String name)
    {
        return this.dictionary.get(name);
    }

    public Index<T> set(String name, T value)
    {
        this.dictionary.put(name, value);
        return this;
    }

    public Index<T> remove(String name)
    {
        this.dictionary.remove(name);
        return this;
    }

    public int size()
    {
        return this.dictionary.size();
    }

    public Map<String, T> dictionary()
    {
        return this.dictionary;
    }

    public Set<String> keys()
    {
        return this.dictionary.keySet();
    }

    public Collection<T> values()
    {
        return this.dictionary.values();
    }
}
