package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.definition.Definitions;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/24
 */
public class VariableRegistrationModule implements VaultModule
{
    @Override
    public String identifier()
    {
        return "variables";
    }

    @Override
    public void process(ObjectNode json, Definitions definitions)
    {
        json.fields().forEachRemaining(entry -> definitions.setVariable(entry.getKey(), value(entry.getValue())));
    }

    /**
     * FIXME: the "long" vs "int" and "double" vs "float" output question is still open
     * FIXME: it is worth noting that this method was implemented after the first (weird?) design decision to always interpret "int" and "double" values out of JSON files and this method tried to remain BC-break free.
     *
     * @param node
     * @return
     */
    public static Object value(JsonNode node)
    {
        if (node.isNull())
            return null;
        if (node.isTextual())
            return node.asText();
        if (node.isBoolean())
            return node.asBoolean();
        if (node.isInt())
            return node.asInt();
        if (node.isLong())
            return node.asLong();
        if (node.isDouble() || node.isFloat())
            return node.asDouble();
        return node.asText();
    }
}
