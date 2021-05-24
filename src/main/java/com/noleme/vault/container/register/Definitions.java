package com.noleme.vault.container.register;

import com.noleme.vault.container.register.index.Scopes;
import com.noleme.vault.container.register.index.Services;
import com.noleme.vault.container.register.index.Tags;
import com.noleme.vault.container.register.index.Variables;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 26/06/2016
 */
public class Definitions
{
    private final Variables variables;
    private final Services services;
    private final Scopes scopes;
    private final Tags tags;

    public Definitions()
    {
        this.variables = new Variables();
        this.services = new Services();
        this.scopes = new Scopes();
        this.tags = new Tags();
    }

    @Deprecated public Variables getVariables() { return this.variables(); }
    @Deprecated public Services getDefinitions() { return this.services(); }

    /**
     * Returns a container indexing variables by their identifiers.
     *
     * @return the Variables container
     */
    public Variables variables()
    {
        return this.variables;
    }

    /**
     * Returns a container indexing services by their identifiers.
     *
     * @return the Services container
     */
    public Services services()
    {
        return this.services;
    }

    /**
     * Returns a container indexing scopes by their identifiers.
     *
     * @return the Scopes container
     */
    public Scopes scopes()
    {
        return this.scopes;
    }

    /**
     * Returns a container indexing tags by their identifiers.
     * This can be used in a VaultModule for performing operations over tagged services.
     *
     * @return the Tags container
     */
    public Tags tags()
    {
        return this.tags;
    }
}
