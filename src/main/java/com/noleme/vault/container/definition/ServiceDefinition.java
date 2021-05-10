package com.noleme.vault.container.definition;

import com.noleme.vault.container.Invocation;

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
    protected Set<String> dependencies = new HashSet<>();

    public String getIdentifier()
    {
        return this.identifier;
    }

    public Set<String> getDependencies()
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
            if (o instanceof String && !((String)o).isEmpty() && ((String)o).startsWith("@"))
                this.dependencies.add(((String)o).substring(1));
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
                if (o instanceof String && !((String)o).isEmpty() && ((String)o).startsWith("@"))
                {
                    String dep = ((String)o).substring(1);
                    this.dependencies.add(dep);
                }
            }
        }
    }
}
