package com.noleme.vault.container.register;

import java.util.UUID;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 15/05/2021
 */
public class ScopedDefinitions extends Definitions
{
    private final String scope;
    private final String uid;

    public ScopedDefinitions(String scope)
    {
        this.scope = scope;
        this.uid = UUID.randomUUID().toString();
    }

    public String scope()
    {
        return this.scope;
    }

    public String uid()
    {
        return this.uid;
    }

    public ScopedDefinitions applyScope()
    {
        this.services().scopeWith(this.uid);

        return this;
    }
}
