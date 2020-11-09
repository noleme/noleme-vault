package com.lumio.vault.container.definition;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 21/08/2015.
 */
public class ServiceAlias extends ServiceDefinition
{
    private final String target;

    /**
     *
     * @param identifier
     * @param target
     */
    public ServiceAlias(String identifier, String target)
    {
        this.identifier = identifier;
        this.target = target;
        this.dependencies.add(target);
    }

    public String getTarget()   { return this.target; }

    @Override
    public void syncDependencies()
    {
        super.syncDependencies();

        this.dependencies.add(this.target);
    }
}
