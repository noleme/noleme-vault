package com.noleme.vault.container.definition;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 15/08/2021
 */
public class ServiceValue <T> extends ServiceDefinition
{
    private final T value;

    public ServiceValue(String identifier, T value)
    {
        this.identifier = identifier;
        this.value = value;
    }

    public T getValue()
    {
        return this.value;
    }
}
