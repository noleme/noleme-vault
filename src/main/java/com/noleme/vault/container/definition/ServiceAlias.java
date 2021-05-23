package com.noleme.vault.container.definition;

import com.noleme.vault.container.register.index.Reference;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 21/08/2015.
 */
public class ServiceAlias extends ServiceDefinition
{
    private final Reference target;

    /**
     *
     * @param identifier
     * @param target
     */
    public ServiceAlias(String identifier, Reference target)
    {
        this.identifier = identifier;
        this.target = target;
        this.dependencies.add(target);
    }

    public Reference getTarget()
    {
        return this.target;
    }

    @Override
    public void syncDependencies()
    {
        super.syncDependencies();

        this.dependencies.add(this.target);
    }

    @Override
    public String toString()
    {
        return "ServiceAlias#"+this.identifier+"->"+this.target;
    }
}
