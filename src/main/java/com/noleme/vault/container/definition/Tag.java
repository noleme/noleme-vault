package com.noleme.vault.container.definition;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 08/05/2021
 */
public class Tag
{
    private final String identifier;
    private final String service;
    private final ObjectNode node;

    public Tag(String identifier, String service, ObjectNode node)
    {
        this.identifier = identifier;
        this.service = service;
        this.node = node;
    }

    public String getIdentifier()
    {
        return this.identifier;
    }

    public String getService()
    {
        return this.service;
    }

    public ObjectNode getNode()
    {
        return this.node;
    }
}
