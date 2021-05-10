package com.noleme.vault.container.definition;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 08/05/2021
 */
public class Tag
{
    private final String identifier;
    private final String service;

    public Tag(String identifier, String service)
    {
        this.identifier = identifier;
        this.service = service;
    }

    public String getIdentifier()
    {
        return this.identifier;
    }

    public String getService()
    {
        return this.service;
    }
}
