package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.Invocation;
import com.noleme.vault.container.definition.*;
import com.noleme.vault.exception.VaultParserException;

import static com.noleme.vault.parser.module.VariableRegistrationModule.value;
import static com.noleme.commons.function.RethrowConsumer.rethrower;

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
            ObjectNode serviceNode = (ObjectNode) entry.getValue();

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
            def = this.generateAlias(definition);
        else if (definition.has("method"))
            def = this.generateProvider(definition);
        else
            def = this.generateInstantiation(definition);

        definitions.getDefinitions().set(def.getIdentifier(), def);
    }

    /**
     *
     * @param definition
     * @return
     */
    private ServiceDefinition generateAlias(ObjectNode definition) throws VaultParserException
    {
        String identifier = definition.get("identifier").asText();
        String target = definition.get("alias").asText();

        if (definition.has("constructor"))
            throw new VaultParserException("An 'alias' declaration cannot declare a 'constructor' property, it must be handled on the parent declaration.");

        ServiceAlias def = new ServiceAlias(identifier, target);
        this.extractInvocations(definition, def);

        if (definition.has("closeable"))
            throw new VaultParserException("The 'closeable' property cannot be used on an alias declaration.");

        return def;
    }

    /**
     *
     * @param definition
     * @return
     */
    private ServiceDefinition generateProvider(ObjectNode definition)
    {
        String identifier = definition.get("identifier").asText();
        String className = definition.get("class").asText();
        String methodName = definition.get("method").asText();

        ServiceProvider def = new ServiceProvider(identifier, className, methodName);
        this.extractMethod(definition, def);
        this.extractInvocations(definition, def);

        if (definition.has("closeable") && definition.get("closeable").asBoolean())
            def.setCloseable(true);

        return def;
    }

    /**
     *
     * @param definition
     * @return
     */
    private ServiceDefinition generateInstantiation(ObjectNode definition)
    {
        String identifier = definition.get("identifier").asText();
        String className = definition.get("class").asText();

        ServiceInstantiation def = new ServiceInstantiation(identifier, className);
        this.extractConstructor(definition, def);
        this.extractInvocations(definition, def);

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
        if (definition.has("constructor"))
        {
            ArrayNode params = (ArrayNode) definition.get("constructor");
            Object[] ctorParams = new Object[params.size()];
            for (int pi = 0 ; pi < params.size() ; ++pi)
                ctorParams[pi] = value(params.get(pi));
            def.setCtorParams(ctorParams);
        }
    }

    /**
     *
     * @param definition
     * @param def
     */
    private void extractMethod(ObjectNode definition, ServiceProvider def)
    {
        if (definition.has("arguments"))
        {
            ArrayNode args = (ArrayNode) definition.get("arguments");
            Object[] methodArgs = new Object[args.size()];
            for (int pi = 0 ; pi < args.size() ; ++pi)
                methodArgs[pi] = value(args.get(pi));
            def.setMethodArgs(methodArgs);
        }
    }

    /**
     *
     * @param definition
     * @param def
     */
    private void extractInvocations(ObjectNode definition, ServiceDefinition def)
    {
        if (definition.has("invocations"))
        {
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
    }
}
