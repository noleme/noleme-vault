package com.noleme.vault.container.definition;

import com.noleme.vault.container.register.index.Reference;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 09/05/2021
 */
public class ServiceTag extends ServiceDefinition
{
    private final List<Reference> entries;

    public ServiceTag(String identifier)
    {
        this.identifier = identifier;
        this.entries = new ArrayList<>();
    }

    public List<Reference> getEntries()
    {
        return this.entries;
    }

    public ServiceTag addEntry(Reference entry)
    {
        this.entries.add(entry);
        this.dependencies.add(entry);
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
