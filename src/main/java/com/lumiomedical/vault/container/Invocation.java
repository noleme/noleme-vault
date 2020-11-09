package com.lumiomedical.vault.container;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 16/10/2014
 */
public class Invocation
{
    private String methodName;
    private Object[] params;

    public Invocation(String methodName)
    {
        this(methodName, new Object[]{});
    }
    public Invocation(String methodName, Object[] params)
    {
        this.methodName = methodName;
        this.params = params;
    }

    public String   getMethodName() { return this.methodName; }
    public Object[] getParams()     { return this.params; }

    public void setMethodName(String mn)    { this.methodName = mn; }
    public void setParams(Object[] ps)      { this.params = ps; }
}
