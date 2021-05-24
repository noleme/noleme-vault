package com.noleme.vault.container.definition;

import com.noleme.vault.container.Invocation;
import com.noleme.vault.container.register.index.Reference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 21/08/2015.
 */
public abstract class ServiceDefinition
{
    protected String identifier;
    protected List<Invocation> invocations = new ArrayList<>();
    protected Set<Reference> dependencies = new HashSet<>();

    public String getIdentifier()
    {
        return this.identifier;
    }

    public Set<Reference> getDependencies()
    {
        return this.dependencies;
    }

    public List<Invocation> getInvocations()
    {
        return this.invocations;
    }

    public ServiceDefinition setIdentifier(String identifier)
    {
        this.identifier = identifier;
        return this;
    }

    public ServiceDefinition addInvocation(Invocation invocation)
    {
        for (Object o : invocation.getParams())
        {
            if (o instanceof Reference)
                this.dependencies.add((Reference) o);
        }
        this.invocations.add(invocation);
        return this;
    }

    /**
     *
     */
    public void syncDependencies()
    {
        this.dependencies = new HashSet<>();

        for (Invocation invocation : this.invocations)
        {
            for (Object o : invocation.getParams())
            {
                if (o instanceof Reference)
                    this.dependencies.add((Reference) o);
            }
        }
    }

    @Override
    public String toString()
    {
        return "ServiceDefinition#"+this.getIdentifier();
    }
}
