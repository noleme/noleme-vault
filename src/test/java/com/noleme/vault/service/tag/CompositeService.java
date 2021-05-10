package com.noleme.vault.service.tag;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 06/05/2021
 */
public class CompositeService
{
    private final Collection<ComponentService> components;

    public CompositeService(Collection<ComponentService> components)
    {
        this.components = components;
    }

    public int size()
    {
        return this.components.size();
    }

    public boolean contains(String value)
    {
        Set<String> values = this.components.stream()
            .map(ComponentService::value)
            .collect(Collectors.toSet())
        ;

        return values.contains(value);
    }
}
