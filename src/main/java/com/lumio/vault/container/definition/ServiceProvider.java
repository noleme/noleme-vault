package com.lumio.vault.container.definition;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/29
 */
public class ServiceProvider extends ServiceDefinition
{
    private String type;
    private String method;
    private Object[] methodArgs;
    private boolean closeable;

    /**
     *
     * @param identifier
     * @param type
     * @param method
     */
    public ServiceProvider(String identifier, String type, String method)
    {
        this.identifier = identifier;
        this.type = type;
        this.method = method;
        this.closeable = false;
    }

    public String getType()
    {
        return this.type;
    }

    public String getMethod()
    {
        return this.method;
    }

    public Object[] getMethodArgs()
    {
        return this.methodArgs;
    }

    public boolean isCloseable()
    {
        return this.closeable;
    }

    /**
     *
     * @param type
     */
    public ServiceProvider setType(String type)
    {
        this.type = type;
        return this;
    }

    /**
     *
     * @param method
     */
    public ServiceProvider setMethod(String method)
    {
        this.method = method;
        return this;
    }

    /**
     *
     * @param methodArgs
     */
    public ServiceProvider setMethodArgs(Object[] methodArgs)
    {
        for (Object o : methodArgs)
        {
            if (o instanceof String && !((String)o).isEmpty() && ((String)o).startsWith("@"))
                this.dependencies.add(((String)o).substring(1));
        }
        this.methodArgs = methodArgs;
        return this;
    }

    /**
     *
     * @param closeable
     * @return
     */
    public ServiceProvider setCloseable(boolean closeable)
    {
        this.closeable = closeable;
        return this;
    }

    @Override
    public void syncDependencies()
    {
        super.syncDependencies();

        for (Object o : this.methodArgs)
        {
            if (o instanceof String && !((String)o).isEmpty() && ((String)o).startsWith("@"))
            {
                String dep = ((String)o).substring(1);
                if (!this.dependencies.contains(dep))
                    this.dependencies.add(dep);
            }
        }
    }
}
