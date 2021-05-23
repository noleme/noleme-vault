package com.noleme.vault.container.definition;

import com.noleme.vault.container.register.index.Reference;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 20/05/2021
 */
public class ServiceScopedImport extends ServiceDefinition
{
    private final String scope;
    private final Reference service;

    /**
     *
     * @param identifier
     * @param scope
     * @param service
     */
    public ServiceScopedImport(String identifier, String scope, Reference service)
    {
        this.identifier = identifier;
        this.scope = scope;
        this.service = service;
        this.dependencies.add(service);
    }

    public String getScope()
    {
        return this.scope;
    }

    public Reference getService()
    {
        return this.service;
    }

    @Override
    public void syncDependencies()
    {
        super.syncDependencies();

        this.dependencies.add(this.service);
    }

    @Override
    public String toString()
    {
        return "ServiceScopedImport#"+this.identifier;
    }
}
