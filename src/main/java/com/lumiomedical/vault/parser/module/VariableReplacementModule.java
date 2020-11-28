package com.lumiomedical.vault.parser.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.VaultParserException;

import static com.lumiomedical.vault.parser.module.VariableResolvingModule.replace;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/26
 */
public class VariableReplacementModule implements VaultModule
{
    @Override
    public String identifier()
    {
        /* This module will perform modifications over the whole JSON tree */
        return "*";
    }

    @Override
    public void process(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        traverseAndReplace(json, definitions);
    }

    /**
     *
     * @param node
     * @param definitions
     * @throws VaultParserException
     */
    private static void traverseAndReplace(ObjectNode node, Definitions definitions) throws VaultParserException
    {
        var keyIterator = node.fieldNames();
        while (keyIterator.hasNext())
        {
            String key = keyIterator.next();
            JsonNode child = node.get(key);

            if (child.isObject())
                traverseAndReplace((ObjectNode)child, definitions);
            else if (child.isArray())
                traverseAndReplace((ArrayNode)child, definitions);
            else if (child.isTextual())
                node.set(key, replace(child.asText(), definitions));
        }
    }

    /**
     *
     * @param node
     * @param definitions
     * @throws VaultParserException
     */
    private static void traverseAndReplace(ArrayNode node, Definitions definitions) throws VaultParserException
    {
        for (int i = 0 ; i < node.size() ; ++i)
        {
            JsonNode child = node.get(i);

            if (child.isObject())
                traverseAndReplace((ObjectNode)child, definitions);
            else if (child.isArray())
                traverseAndReplace((ArrayNode)child, definitions);
            else if (child.isTextual())
                node.set(i, replace(child.asText(), definitions));
        }
    }
}
