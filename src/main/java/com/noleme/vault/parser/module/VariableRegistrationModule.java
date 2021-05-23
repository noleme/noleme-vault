package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.RuntimeVaultException;
import com.noleme.vault.exception.VaultParserException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void process(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        try {
            json.fields().forEachRemaining(entry -> definitions.variables().set(entry.getKey(), valueOrContainer(entry.getValue())));
        }
        catch (RuntimeVaultException e) {
            throw new VaultParserException(e.getMessage(), e);
        }
    }

    /**
     *
     * @param node
     * @return
     */
    public static Object valueOrContainer(JsonNode node)
    {
        if (node.isArray())
            return arrayAsList((ArrayNode)node);
        if (node.isObject())
            return objectAsMap((ObjectNode)node);
        return value(node);
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

    /**
     *
     * @param node
     * @return
     */
    public static List<Object> arrayAsList(ArrayNode node)
    {
        List<Object> list = new ArrayList<>(node.size());
        node.elements().forEachRemaining(n -> {
            if (n.isObject() || n.isArray())
                throw new RuntimeVaultException("List variables cannot contain array nor object values.");
            list.add(value(n));
        });
        return list;
    }

    /**
     *
     * @param node
     * @return
     */
    public static Map<String, Object> objectAsMap(ObjectNode node)
    {
        Map<String, Object> map = new HashMap<>();
        node.fields().forEachRemaining(e -> {
            if (e.getValue().isObject() || e.getValue().isArray())
                throw new RuntimeVaultException("Object variables cannot contain array nor object values.");
            map.put(e.getKey(), value(e.getValue()));
        });
        return map;
    }
}
