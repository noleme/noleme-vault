package com.noleme.vault.container.definition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 09/05/2021
 */
public class ServiceTag extends ServiceDefinition
{
    private final List<String> entries;

    public ServiceTag(String identifier)
    {
        this.identifier = identifier;
        this.entries = new ArrayList<>();
    }

    public List<String> getEntries()
    {
        return this.entries;
    }

    public ServiceTag addEntry(String identifier)
    {
        this.entries.add(identifier);
        this.dependencies.add(identifier);
        return this;
    }

    @Override
    public void syncDependencies()
    {
        super.syncDependencies();

        this.dependencies.addAll(this.entries);
    }

    @Override
    public String toString()
    {
        return "ServiceTag#"+this.identifier+"("+this.entries.size()+")";
    }
}
