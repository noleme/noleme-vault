package com.noleme.vault.container.definition;

import com.noleme.vault.container.Invocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 21/08/2015.
 */
public abstract class ServiceDefinition
{
    protected String identifier;
    protected List<Invocation> invocations = new ArrayList<>();
    protected List<String> dependencies = new ArrayList<>();

    public String getIdentifier()
    {
        return this.identifier;
    }

    public List<String> getDependencies()
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

    /**
     *
     * @param invocation
     */
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
        this.dependencies = new ArrayList<>();

        for (Invocation invocation : this.invocations)
        {
            for (Object o : invocation.getParams())
            {
                if (o instanceof String && !((String)o).isEmpty() && ((String)o).startsWith("@"))
                {
                    String dep = ((String)o).substring(1);
                    if (!this.dependencies.contains(dep))
                        this.dependencies.add(dep);
                }
            }
        }
    }
}
