package com.noleme.vault.container.definition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 26/06/2016
 */
public class Definitions
{
    private final Variables variables;
    private final Services definitions;

    public Definitions()
    {
        this.variables = new Variables();
        this.definitions = new Services();
    }

    /**
     *
     * @return
     */
    public Variables getVariables()
    {
        return this.variables;
    }

    /**
     *
     * @return
     */
    public Services getDefinitions()
    {
        return this.definitions;
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

        public Object get(String name)
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
