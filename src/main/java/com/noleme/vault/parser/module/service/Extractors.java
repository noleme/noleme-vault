package com.noleme.vault.parser.module.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.json.Json;
import com.noleme.vault.container.Invocation;
import com.noleme.vault.container.definition.ServiceDefinition;
import com.noleme.vault.container.definition.ServiceInstantiation;
import com.noleme.vault.container.definition.ServiceProvider;
import com.noleme.vault.container.definition.Tag;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.register.index.Tags;
import com.noleme.vault.exception.VaultParserException;

import static com.noleme.vault.parser.module.VariableRegistrationModule.valueOrContainer;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public final class Extractors
{
    private Extractors() {}

    /**
     *
     * @param definition
     * @param def
     */
    public static void extractConstructor(ObjectNode definition, ServiceInstantiation def, Definitions definitions)
    {
        if (!definition.has("constructor"))
            return;

        ArrayNode params = (ArrayNode) definition.get("constructor");
        Object[] ctorParams = new Object[params.size()];
        for (int pi = 0 ; pi < params.size() ; ++pi)
        {
            Object value = valueOrContainer(params.get(pi));
            if (value instanceof String)
                value = extractReferenceIfFound((String) value, definitions);

            ctorParams[pi] = value;
        }
        def.setCtorParams(ctorParams);
    }

    /**
     *
     * @param definition
     * @param def
     */
    public static void extractMethod(ObjectNode definition, ServiceProvider def, Definitions definitions)
    {
        if (!definition.has("arguments"))
            return;

        ArrayNode args = (ArrayNode) definition.get("arguments");
        Object[] methodArgs = new Object[args.size()];
        for (int pi = 0 ; pi < args.size() ; ++pi)
        {
            Object value = valueOrContainer(args.get(pi));
            if (value instanceof String)
                value = extractReferenceIfFound((String) value, definitions);

            methodArgs[pi] = value;
        }
        def.setMethodArgs(methodArgs);
    }

    /**
     *
     * @param definition
     * @param def
     */
    public static void extractInvocations(ObjectNode definition, ServiceDefinition def, Definitions definitions)
    {
        if (!definition.has("invocations"))
            return;

        for (JsonNode invocationNode : definition.get("invocations"))
        {
            ArrayNode inv = (ArrayNode) invocationNode;
            if (inv.size() == 0)
                continue;
            Invocation invocation = new Invocation(inv.get(0).asText());
            if (inv.size() > 1)
            {
                Object[] methodParams = new Object[inv.size() - 1];
                for (int mpi = 1 ; mpi < inv.size() ; ++mpi)
                {
                    Object value = valueOrContainer(inv.get(mpi));
                    if (value instanceof String)
                        value = extractReferenceIfFound((String) value, definitions);

                    methodParams[mpi - 1] = value;
                }
                invocation.setParams(methodParams);
            }
            def.addInvocation(invocation);
        }
    }

    /**
     *
     * @param definition
     * @param tags
     * @throws VaultParserException
     */
    public static void extractTags(ObjectNode definition, Tags tags) throws VaultParserException
    {
        if (!definition.has("tags"))
            return;

        String serviceIdentifier = definition.get("identifier").asText();

        for (JsonNode tagNode : definition.get("tags"))
        {
            ObjectNode node = tagNode.isTextual()
                ? Json.newObject().put("id", tagNode.asText())
                : (ObjectNode) tagNode
            ;

            if (!node.has("id"))
                throw new VaultParserException("Service "+serviceIdentifier+" has an object tag declaration without and 'id'.");

            String id = node.get("id").asText();

            tags.register(new Tag(id, serviceIdentifier, node));
        }
    }

    /**
     *
     * @param value
     * @param definitions
     * @return
     */
    private static Object extractReferenceIfFound(String value, Definitions definitions)
    {
        if (value.isBlank() || !value.startsWith("@"))
            return value;

        return definitions.services().reference(value.substring(1));
    }
}
