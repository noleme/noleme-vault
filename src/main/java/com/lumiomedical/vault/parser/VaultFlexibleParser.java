package com.lumiomedical.vault.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.json.JsonException;
import com.lumiomedical.vault.container.Invocation;
import com.lumiomedical.vault.container.definition.*;
import com.lumiomedical.vault.exception.VaultParserException;
import com.lumiomedical.vault.exception.VaultStructureException;
import com.lumiomedical.vault.parser.resolver.FlexibleResolver;
import com.lumiomedical.vault.parser.resolver.VaultResolver;
import com.lumiomedical.vault.parser.resolver.source.Source;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.noleme.commons.function.RethrowConsumer.rethrower;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 05/02/15.
 */
public class VaultFlexibleParser implements VaultParser
{
    private final VaultResolver resolver;
    private static final Set<String> directives = Set.of(
        "variables", "imports", "services"
    );

    public VaultFlexibleParser()
    {
        this(new FlexibleResolver());
    }

    /**
     *
     * @param resolver
     */
    public VaultFlexibleParser(VaultResolver resolver)
    {
        this.resolver = resolver;
    }

    @Override
    public Definitions extract(String path, Definitions definitions) throws VaultParserException
    {
        try {
            Source source = this.resolver.resolve(path);
            return this.extract(source, definitions);
        }
        catch (JsonException e) {
            throw new VaultParserException("The configuration file could not be loaded, the input appears to contain invalid JSON.", e);
        }
    }

    @Override
    public Definitions extract(Source source, Definitions definitions) throws VaultParserException
    {
        try {
            ObjectNode json = source.interpret();

            this.validateStructure(json);
            for (String imp : this.getImports(json))
            {
                Source importSource = this.resolver.resolve(imp);
                this.extract(importSource, definitions);
            }
            this.extractVariables(json, definitions);
            this.extractServices(json, definitions);

            return definitions;
        }
        catch (VaultStructureException e) {
            throw new VaultParserException("A structural error has been detected in input "+source.getOrigin()+".", e);
        }
    }

    /**
     *
     * @param json
     */
    private void validateStructure(ObjectNode json) throws VaultStructureException
    {
        Iterator<String> keys = json.fieldNames();
        while (keys.hasNext())
        {
            String key = keys.next();
            if (!directives.contains(key))
                throw new VaultStructureException("An unknown key '"+key+"' has been defined.");
        }
    }

    /**
     *
     * @param json
     * @return
     */
    private List<String> getImports(ObjectNode json)
    {
        List<String> found = new ArrayList<>();
        if (json.has("imports"))
        {
            for (JsonNode node : json.get("imports"))
                found.add(node.asText());
        }
        return found;
    }

    /**
     *
     * @param json
     * @param definitions
     */
    private void extractVariables(ObjectNode json, Definitions definitions)
    {
        if (json.has("variables") && !json.get("variables").isNull())
        {
            ObjectNode vars = (ObjectNode) json.get("variables");
            vars.fields().forEachRemaining(entry -> definitions.setVariable(entry.getKey(), value(entry.getValue())));
        }
    }

    /**
     *
     * @param json
     * @param definitions
     * @throws VaultParserException
     */
    private void extractServices(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        if (json.has("services"))
        {
            JsonNode servicesJson = json.get("services");

            /*
             * Here we allow both object and array notations.
             * If the old-style array notation is used, we expect an 'identifier' field in the declaration object.
             * If the object notation is used, the declaration's key can be used instead of an explicit 'identifier' field.
             * If both the key and identifier are present with conflicting values, an error will be thrown.
             */
            if (servicesJson.isArray())
            {
                for (JsonNode serviceNode : servicesJson)
                    this.extractService((ObjectNode)serviceNode, definitions);
            }
            else if (servicesJson.isObject())
            {
                servicesJson.fields().forEachRemaining(rethrower(entry -> {
                    String identifier = entry.getKey();
                    ObjectNode serviceNode = (ObjectNode) entry.getValue();

                    if (serviceNode.has("identifier") && !identifier.equals(serviceNode.get("identifier").asText()))
                        throw new VaultParserException("A service was declared with conflicting identifiers, the shorthand notation '"+identifier+"' is different from the 'identifier' field of value '"+serviceNode.get("identifier").asText()+"' found in the declaration ");

                    serviceNode.put("identifier", identifier);

                    this.extractService(serviceNode, definitions);
                }));
            }
            else
                throw new VaultParserException("The 'services' entry is expected to be either an array or an object.");
        }
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

        definitions.setDefinition(def.getIdentifier(), def);
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

    /**
     * FIXME: the "long" vs "int" and "double" vs "float" output question is still open
     * FIXME: it is worth noting that this method was implemented after the first (weird?) design decision to always interpret "int" and "double" values out of JSON files and this method tried to remain BC-break free.
     *
     * @param node
     * @return
     */
    private static Object value(JsonNode node)
    {
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
