package com.lumio.vault.container;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 17/07/2014
 */
public class Dependency
{
    private final String identifier;
    private final String setterName;
    private final Type type;

    public Dependency(String identifier, String setterName, Type type)
    {
        this.identifier = identifier;
        this.setterName = setterName;
        this.type = type;
    }

    public String getIdentifier()
    {
        return this.identifier;
    }

    public String getSetterName()
    {
        return this.setterName;
    }

    public Type getType()
    {
        return this.type;
    }

    public enum Type
    {
        VARIABLE,
        SERVICE
    }
}
