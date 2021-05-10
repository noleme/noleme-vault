package com.noleme.vault.container.definition;

import java.util.*;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 26/06/2016
 */
public class Definitions
{
    private final Variables variables;
    private final Services services;
    private final Tags tags;

    public Definitions()
    {
        this.variables = new Variables();
        this.services = new Services();
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
     * Returns a container indexing tags by their identifiers.
     * This can be used in a VaultModule for performing operations over tagged services.
     *
     * @return the Tags container
     */
    public Tags tags()
    {
        return this.tags;
    }

    public static class Variables extends Dictionary<Object>
    {
        @Override
        public Variables set(String name, Object value)
        {
            return (Variables) super.set(name, value);
        }

        @Override
        public Variables remove(String name)
        {
            return (Variables) super.remove(name);
        }
    }

    public static class Services extends Dictionary<ServiceDefinition>
    {
        @Override
        public Services set(String name, ServiceDefinition value)
        {
            return (Services) super.set(name, value);
        }

        @Override
        public Services remove(String name)
        {
            return (Services) super.remove(name);
        }
    }

    public static class Tags
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

    private static abstract class Dictionary <T>
    {
        private final Map<String, T> dictionary;

        private Dictionary()
        {
            this.dictionary = new HashMap<>();
        }

        public boolean has(String name)
        {
            return this.dictionary.containsKey(name);
        }

        public T get(String name)
        {
            return this.dictionary.get(name);
        }

        public Dictionary<T> set(String name, T value)
        {
            this.dictionary.put(name, value);
            return this;
        }

        public Dictionary<T> remove(String name)
        {
            this.dictionary.remove(name);
            return this;
        }

        public int size()
        {
            return this.dictionary.size();
        }

        public Map<String, T> dictionary()
        {
            return this.dictionary;
        }

        public Collection<T> values()
        {
            return this.dictionary.values();
        }
    }
}
