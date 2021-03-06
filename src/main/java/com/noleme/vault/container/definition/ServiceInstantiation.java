package com.noleme.vault.container.definition;

import com.noleme.vault.container.register.index.Reference;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 13/09/2014.
 */
public class ServiceInstantiation extends ServiceDefinition
{
    private String type;
    private Object[] ctorParams;
    private boolean closeable;

    /**
     *
     * @param identifier
     * @param type
     */
    public ServiceInstantiation(String identifier, String type)
    {
        this(identifier, type, new Object[0]);
    }

    /**
     *
     * @param identifier
     * @param type
     * @param ctorParams
     */
    public ServiceInstantiation(String identifier, String type, Object[] ctorParams)
    {
        this.identifier = identifier;
        this.type = type;
        this.setCtorParams(ctorParams);
        this.closeable = false;
    }

    public String getType()
    {
        return this.type;
    }

    public Object[] getCtorParams()
    {
        return this.ctorParams;
    }

    public boolean isCloseable()
    {
        return this.closeable;
    }

    /**
     *
     * @param type
     */
    public ServiceInstantiation setType(String type)
    {
        this.type = type;
        return this;
    }

    /**
     *
     * @param ctorParams
     */
    public ServiceInstantiation setCtorParams(Object[] ctorParams)
    {
        for (Object o : ctorParams)
        {
            if (o instanceof Reference)
                this.dependencies.add((Reference) o);
        }
        this.ctorParams = ctorParams;
        return this;
    }

    /**
     *
     * @param closeable
     * @return
     */
    public ServiceInstantiation setCloseable(boolean closeable)
    {
        this.closeable = closeable;
        return this;
    }

    @Override
    public void syncDependencies()
    {
        super.syncDependencies();

        for (Object o : this.ctorParams)
        {
            if (o instanceof Reference)
                this.dependencies.add((Reference) o);
        }
    }

    @Override
    public String toString()
    {
        return "ServiceInstantiation#"+this.identifier+"("+this.type+")";
    }
}
