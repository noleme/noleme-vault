package com.noleme.vault.container.definition;

import java.util.Collection;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 26/06/2016
 */
public class Variable
{
    private final String name;
    private final Collection<String> dependencies;
    private Object value;

    /**
     *
     * @param name
     * @param value
     * @param dependencies
     */
    public Variable(String name, Object value, Collection<String> dependencies)
    {
        this.name = name;
        this.value = value;
        this.dependencies = dependencies;
    }

    /**
     *
     * @return
     */
    public String getName()
    {
        return this.name;
    }

    /**
     *
     * @return
     */
    public Object getValue()
    {
        return this.value;
    }

    /**
     *
     * @param value
     */
    public Variable setValue(Object value)
    {
        this.value = value;
        return this;
    }

    /**
     *
     * @return
     */
    public boolean hasDependencies()
    {
        return !this.dependencies.isEmpty();
    }

    /**
     *
     * @return
     */
    public Collection<String> getDependencies()
    {
        return this.dependencies;
    }
}
