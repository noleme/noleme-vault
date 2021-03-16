package com.lumiomedical.vault.container;

import com.lumiomedical.vault.exception.RuntimeVaultException;
import com.lumiomedical.vault.exception.VaultNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 05/02/15.
 */
public class Cellar implements AutoCloseable
{
    private final Map<String, Object> services;
    private final Map<String, Object> variables;
    private final Set<String> closeables;

    private static final Logger logger = LoggerFactory.getLogger(Cellar.class);

    public Cellar()
    {
        this.services = new HashMap<>();
        this.variables = new HashMap<>();
        this.closeables = new HashSet<>();

        this.services.put("cellar", this);
    }

    /**
     *
     * @param name
     * @return
     */
    public Object get(String name)
    {
        if (this.hasService(name))
            return this.getService(name);
        else if (this.hasVariable(name))
            return this.getVariable(name);
        throw new VaultNotFoundException("The cellar has no declared \""+name+"\" service or variable.");
    }

    /**
     *
     * @param name
     * @param type
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String name, Class<T> type)
    {
        Object value = this.get(name);

        if (!type.isInstance(value))
            return (T)value;

        throw new RuntimeVaultException("The \""+name+"\" service or variable cannot be cast to type "+type.getSimpleName()+".");
    }

    /**
     *
     * @param name
     * @return
     */
    public Object has(String name)
    {
        return this.hasService(name) || this.hasVariable(name);
    }

    public boolean isEmpty()
    {
        return this.services.isEmpty() && this.variables.isEmpty();
    }

    /*
    ** Services
    */

    /**
     *
     * @param name
     * @return
     */
    public Object getService(String name)
    {
        if (!this.services.containsKey(name))
            throw new VaultNotFoundException("The cellar has no declared \""+name+"\" service.");
        return this.services.get(name);
    }

    /**
     *
     * @param name
     * @param type
     * @param <T>
     * @return
     */
    public <T> T getService(String name, Class<T> type)
    {
        Object service = this.getService(name);

        if (!type.isInstance(service))
            throw new VaultNotFoundException("The cellar has a \""+name+"\" service but its type does not match the required "+type.getName());

        return type.cast(service);
    }

    public boolean hasService(String name)
    {
        return this.services.containsKey(name);
    }

    public void putService(String name, Object o)
    {
        this.services.put(name, o);
    }

    public void registerCloseable(String name)
    {
        this.closeables.add(name);
    }

    public boolean isCloseable(String name)
    {
        return this.closeables.contains(name);
    }

    public Map<String, Object> getServices()
    {
        return this.services;
    }

    /*
    ** Variables
    */

    public Object getVariable(String name)
    {
        if (!this.variables.containsKey(name))
            throw new VaultNotFoundException("The cellar has no declared \""+name+"\" variable.");
        return this.variables.get(name);
    }

    public boolean hasVariable(String name)         { return this.variables.containsKey(name); }

    public void putVariable(String name, Object o)  { this.variables.put(name, o); }

    public Map<String, Object> getVariables()       { return this.variables; }

    @Override
    public void close()
    {
        logger.debug("Closing Cellar ({} closeables registered)", this.closeables.size());

        for (String closeable : this.closeables)
        {
            try {
                Object service = this.services.get(closeable);
                if (service instanceof AutoCloseable)
                    ((AutoCloseable)service).close();
            }
            catch (Exception e) {
                System.err.println("The \""+closeable+"\" closeable service could not be successfully closed ("+e.getMessage()+").");
            }
        }
    }
}
