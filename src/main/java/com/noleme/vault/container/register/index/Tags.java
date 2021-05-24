package com.noleme.vault.container.register.index;

import com.noleme.vault.container.definition.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public class Tags
{
    private final Map<String, Set<Tag>> tagsByIdentifier;
    private final Map<String, Set<Tag>> tagsByService;

    public Tags()
    {
        this.tagsByIdentifier = new HashMap<>();
        this.tagsByService = new HashMap<>();
    }

    public Set<String> identifiers()
    {
        return this.tagsByIdentifier.keySet();
    }

    public Set<String> taggedServices()
    {
        return this.tagsByService.keySet();
    }

    public Set<Tag> forIdentifier(String identifier)
    {
        return this.tagsByIdentifier.get(identifier);
    }

    public Set<Tag> forService(String service)
    {
        return this.tagsByService.get(service);
    }

    /**
     * Removes all tags associated with a service, and makes sure the are properly un-indexed.
     *
     * @param identifier A service identifier
     * @return the Tags container
     */
    public Tags clearTagsForService(String identifier)
    {
        if (!this.tagsByService.containsKey(identifier))
            return this;

        for (Tag tag : this.tagsByService.get(identifier))
            this.tagsByIdentifier.get(tag.getIdentifier()).remove(tag);
        this.tagsByService.remove(identifier);

        return this;
    }

    /**
     * Registers a tag onto a service and indexes it by its tag identifier.
     *
     * @param tag A tag to be registered
     * @return the Tags container
     */
    public Tags register(Tag tag)
    {
        if (!this.tagsByIdentifier.containsKey(tag.getIdentifier()))
            this.tagsByIdentifier.put(tag.getIdentifier(), new HashSet<>());
        if (!this.tagsByService.containsKey(tag.getService()))
            this.tagsByService.put(tag.getService(), new HashSet<>());

        this.tagsByIdentifier.get(tag.getIdentifier()).add(tag);
        this.tagsByService.get(tag.getService()).add(tag);

        return this;
    }
}