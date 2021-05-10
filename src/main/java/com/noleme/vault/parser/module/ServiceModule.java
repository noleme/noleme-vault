package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.json.Json;
import com.noleme.vault.container.Invocation;
import com.noleme.vault.container.definition.*;
import com.noleme.vault.container.definition.Definitions.Tags;
import com.noleme.vault.exception.VaultParserException;

import static com.noleme.vault.parser.module.VariableRegistrationModule.value;
import static com.noleme.commons.function.RethrowConsumer.rethrower;
import static com.noleme.vault.parser.module.VariableRegistrationModule.valueOrContainer;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/24
 */
public class ServiceModule implements VaultModule
{
    @Override
    public String identifier()
    {
        return "services";
    }

    @Override
    public void process(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        json.fields().forEachRemaining(rethrower(entry -> {
            String identifier = entry.getKey();
            JsonNode node = entry.getValue();
            ObjectNode serviceNode = node.isTextual()
                ? Json.newObject().put("marker", node.asText())
                : (ObjectNode) entry.getValue()
            ;

            if (serviceNode.has("identifier") && !identifier.equals(serviceNode.get("identifier").asText()))
                throw new VaultParserException("A service was declared with conflicting identifiers, the shorthand notation '"+identifier+"' is different from the 'identifier' field of value '"+serviceNode.get("identifier").asText()+"' found in the declaration ");

            serviceNode.put("identifier", identifier);

            this.extractService(serviceNode, definitions);
        }));
    }

    /**
     *
     * @param definition
     * @param definitions
     * @throws VaultParserException
     */
    private void extractService(ObjectNode definition, Definitions definitions) throws VaultParserException
    {
        ServiceDefinition def;
        if (definition.has("alias"))
            def = this.generateAlias(definition, definitions.tags());
        else if (definition.has("method"))
            def = this.generateProvider(definition, definitions.tags());
        else if (definition.has("marker"))
            def = this.generateMarkerDefinition(definition, definitions.tags());
        else
            def = this.generateInstantiation(definition, definitions.tags());

        definitions.services().set(def.getIdentifier(), def);
    }

    /**
     *
     * @param definition
     * @param tags
     * @return
     * @throws VaultParserException
     */
    private ServiceDefinition generateAlias(ObjectNode definition, Tags tags) throws VaultParserException
    {
        String identifier = definition.get("identifier").asText();
        String target = definition.get("alias").asText();

        if (definition.has("constructor"))
            throw new VaultParserException("An 'alias' declaration cannot declare a 'constructor' property, it must be handled on the parent declaration.");

        ServiceAlias def = new ServiceAlias(identifier, target);
        this.extractInvocations(definition, def);
        this.extractTags(definition, tags);

        if (definition.has("closeable"))
            throw new VaultParserException("The 'closeable' property cannot be used on an alias declaration.");

        return def;
    }

    /**
     *
     * @param definition
     * @param tags
     * @return
     */
    private ServiceDefinition generateProvider(ObjectNode definition, Tags tags) throws VaultParserException
    {
        String identifier = definition.get("identifier").asText();
        String className = definition.get("class").asText();
        String methodName = definition.get("method").asText();

        ServiceProvider def = new ServiceProvider(identifier, className, methodName);
        this.extractMethod(definition, def);
        this.extractInvocations(definition, def);
        this.extractTags(definition, tags);

        if (definition.has("closeable") && definition.get("closeable").asBoolean())
            def.setCloseable(true);

        return def;
    }

    /**
     * Marker definitions rely on short-hand string notations.
     * As it stands, no feature relies on markers ; tag declarations did for a short lapse, but were moved to a separate module.
     *
     * @param definition
     * @param tags
     * @return
     * @throws VaultParserException
     */
    private ServiceDefinition generateMarkerDefinition(ObjectNode definition, Tags tags) throws VaultParserException
    {
        String marker = definition.get("marker").asText();

        throw new VaultParserException("An unknown marker of type "+marker+" was found.");
    }

    /**
     *
     * @param definition
     * @param tags
     * @return
     * @throws VaultParserException
     */
    private ServiceDefinition generateInstantiation(ObjectNode definition, Tags tags) throws VaultParserException
    {
        String identifier = definition.get("identifier").asText();
        String className = definition.get("class").asText();

        ServiceInstantiation def = new ServiceInstantiation(identifier, className);
        this.extractConstructor(definition, def);
        this.extractInvocations(definition, def);
        this.extractTags(definition, tags);

        if (definition.has("closeable") && definition.get("closeable").asBoolean())
            def.setCloseable(true);

        return def;
    }

    /**
     *
     * @param definition
     * @param def
     */
    private void extractConstructor(ObjectNode definition, ServiceInstantiation def)
    {
        if (!definition.has("constructor"))
            return;

        ArrayNode params = (ArrayNode) definition.get("constructor");
        Object[] ctorParams = new Object[params.size()];
        for (int pi = 0 ; pi < params.size() ; ++pi)
            ctorParams[pi] = valueOrContainer(params.get(pi));
        def.setCtorParams(ctorParams);
    }

    /**
     *
     * @param definition
     * @param def
     */
    private void extractMethod(ObjectNode definition, ServiceProvider def)
    {
        if (!definition.has("arguments"))
            return;

        ArrayNode args = (ArrayNode) definition.get("arguments");
        Object[] methodArgs = new Object[args.size()];
        for (int pi = 0 ; pi < args.size() ; ++pi)
            methodArgs[pi] = valueOrContainer(args.get(pi));
        def.setMethodArgs(methodArgs);
    }

    /**
     *
     * @param definition
     * @param def
     */
    private void extractInvocations(ObjectNode definition, ServiceDefinition def)
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
                    methodParams[mpi - 1] = value(inv.get(mpi));
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
    private void extractTags(ObjectNode definition, Tags tags) throws VaultParserException
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
}
