package com.noleme.vault.parser.resolver.dialect.interpreter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.exception.VaultResolverException;
import com.noleme.json.Json;
import com.noleme.json.JsonException;

import java.io.InputStream;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/28
 */
public class JsonInterpreter implements DialectInterpreter<InputStream>
{
    @Override
    public boolean isExpected(String origin)
    {
        return origin.endsWith(".json");
    }

    @Override
    public boolean canInterpret(Object data)
    {
        return data instanceof InputStream;
    }

    @Override
    public ObjectNode interpret(InputStream data) throws VaultResolverException
    {
        try {
            return (ObjectNode) Json.parse(data);
        }
        catch (JsonException e) {
            throw new VaultResolverException("An error occurred while attempting to interpret the source as JSON.", e);
        }
    }
}
