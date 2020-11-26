package com.lumiomedical.vault.container.definition;

import com.lumiomedical.vault.container.Invocation;
import com.lumiomedical.vault.container.definition.hook.PostResolvingHandler;

import java.util.*;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 26/06/2016
 */
public class Definitions
{
    private final Map<String, Object> variables;
    private final Map<String, ServiceDefinition> definitions;
    private final List<PostResolvingHandler> handlers;

    public Definitions()
    {
        this.variables = new HashMap<>();
        this.definitions = new HashMap<>();
        this.handlers = new ArrayList<>();
    }

    /**
     *
     * @param name
     * @return
     */
    public boolean hasVariable(String name)
    {
        return this.variables.containsKey(name);
    }

    /**
     *
     * @param name
     * @return
     */
    public Object getVariable(String name)
    {
        return this.variables.get(name);
    }

    /**
     *
     * @return
     */
    public Map<String, Object> getVariables()
    {
        return this.variables;
    }

    /**
     *
     * @param name
     * @param value
     */
    public Definitions setVariable(String name, Object value)
    {
        this.variables.put(name, value);
        return this;
    }

    /**
     *
     * @param identifier
     * @return
     */
    public boolean hasDefinition(String identifier)
    {
        return this.definitions.containsKey(identifier);
    }

    /**
     *
     * @return
     */
    public Collection<ServiceDefinition> listDefinitions()
    {
        return this.definitions.values();
    }

    /**
     *
     * @param identifier
     * @param def
     */
    public Definitions setDefinition(String identifier, ServiceDefinition def)
    {
        this.definitions.put(identifier, def);
        return this;
    }

    /**
     *
     * @param identifier
     */
    public Definitions removeDefinition(String identifier)
    {
        this.definitions.remove(identifier);
        return this;
    }

    /**
     *
     * @return
     */
    public List<PostResolvingHandler> getHandlers()
    {
        return this.handlers;
    }

    /**
     *
     * @param handler
     * @return
     */
    public Definitions registerHandler(PostResolvingHandler handler)
    {
        this.handlers.add(handler);
        return this;
    }

    /**
     *
     * @return
     */
    public String dumpContents()
    {
        StringBuilder sb = new StringBuilder();
        if (!this.variables.isEmpty())
        {
            sb.append("variables:\n");
            for (Map.Entry<String, Object> v : this.variables.entrySet())
                sb.append("\t").append(v.getKey()).append(":").append(v.getValue()).append("\n");
        }
        if (!this.definitions.isEmpty())
        {
            sb.append("definitions:\n");
            for (Map.Entry<String, ServiceDefinition> d : this.definitions.entrySet())
            {
                sb.append("\t").append(d.getKey()).append("\n");
                if (!d.getValue().getDependencies().isEmpty())
                {
                    sb.append("\t\tdeps:\n");
                    for (String dep : d.getValue().getDependencies())
                        sb.append("\t\t\t").append(dep).append("\n");
                }
                if (!d.getValue().getInvocations().isEmpty())
                {
                    sb.append("\t\tinvocations:\n");
                    for (Invocation invocation : d.getValue().getInvocations())
                    {
                        sb.append("\t\t\t").append(invocation.getMethodName()).append("\n");
                        for (Object param : invocation.getParams())
                            sb.append("\t\t\t\t- ").append(param.toString()).append("\n");
                    }
                }
                if (d.getValue() instanceof ServiceInstantiation)
                {
                    if (((ServiceInstantiation) d.getValue()).getCtorParams().length > 0)
                    {
                        sb.append("\t\tctor:\n");
                        for (Object param : ((ServiceInstantiation)d.getValue()).getCtorParams())
                            sb.append("\t\t\t").append(param).append("\n");
                    }
                    else
                        sb.append("\t\tctor: no arguments\n");
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : "The Definitions set is empty.";
    }
}
