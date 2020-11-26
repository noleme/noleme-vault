package com.lumiomedical.vault.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.VaultParserException;
import com.lumiomedical.vault.exception.VaultStructureException;
import com.lumiomedical.vault.parser.module.ServiceModule;
import com.lumiomedical.vault.parser.module.VariableModule;
import com.lumiomedical.vault.parser.module.VaultModule;
import com.lumiomedical.vault.parser.resolver.FlexibleResolver;
import com.lumiomedical.vault.parser.resolver.VaultResolver;
import com.lumiomedical.vault.parser.resolver.source.Source;
import com.noleme.commons.container.Lists;
import com.noleme.json.JsonException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 05/02/15.
 */
public class VaultFlexibleParser implements VaultParser
{
    private final VaultResolver resolver;
    private final List<VaultModule> modules = Lists.of(
        new VariableModule(),
        new ServiceModule()
    );
    private static final Set<String> directives = Set.of("imports");

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

            for (VaultModule module : this.modules)
            {
                if (json.has(module.identifier()) && !json.get(module.identifier()).isNull())
                    module.process(json.get(module.identifier()), definitions);
            }

            return definitions;
        }
        catch (VaultStructureException e) {
            throw new VaultParserException("A structural error has been detected in input "+source.getOrigin()+".", e);
        }
    }

    @Override
    public VaultParser register(VaultModule module)
    {
        this.modules.add(module);
        return this;
    }

    /**
     *
     * @param json
     */
    private void validateStructure(ObjectNode json) throws VaultStructureException
    {
        Iterator<String> keys = json.fieldNames();

        Set<String> validKeys = modules.stream()
            .map(VaultModule::identifier)
            .collect(Collectors.toSet())
        ;
        validKeys.addAll(directives);

        while (keys.hasNext())
        {
            String key = keys.next();
            if (!validKeys.contains(key))
                throw new VaultStructureException("An unknown module '"+key+"' has been required, available modules are: "+ String.join(", ", validKeys));
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
}
