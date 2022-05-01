package com.noleme.vault.container.register.index;

import com.noleme.vault.container.definition.ServiceDefinition;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public class Services extends Index<ServiceDefinition>
{
    private final References references;

    public Services()
    {
        this.references = new References();
    }

    @Override
    public Services set(String name, ServiceDefinition value)
    {
        if (!this.references.has(name))
            this.references.set(name, new Reference(name));
        return (Services) super.set(name, value);
    }

    public Services set(ServiceDefinition def)
    {
        return this.set(def.getIdentifier(), def);
    }

    @Override
    public Services remove(String name)
    {
        this.references.remove(name);
        return (Services) super.remove(name);
    }

    public Reference reference(String name)
    {
        if (!this.references.has(name))
            this.references.set(name, new Reference(name));
        return this.references.get(name);
    }

    /**
     *
     * @param scope
     */
    public void scopeWith(String scope)
    {
        Set<String> keys = new HashSet<>(this.keys());
        for (String identifier : keys)
        {
            ServiceDefinition def = this.get(identifier);
            Reference ref = this.references.get(identifier);
            String scoped = scope+"#"+identifier;

            def.setIdentifier(scoped);
            this.remove(identifier);
            this.set(scoped, def);

            ref.setIdentifier(scoped);
            this.references.remove(identifier);
            this.references.set(scoped, ref);
        }
    }
}
