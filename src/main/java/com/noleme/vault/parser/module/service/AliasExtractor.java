package com.noleme.vault.parser.module.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.definition.ServiceAlias;
import com.noleme.vault.container.definition.ServiceDefinition;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultParserException;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public class AliasExtractor implements ServiceDefinitionExtractor
{
    @Override
    public boolean accepts(ObjectNode json)
    {
        return json.has("alias");
    }

    @Override
    public ServiceDefinition extract(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        String identifier = json.get("identifier").asText();
        String target = json.get("alias").asText();

        if (json.has("constructor"))
            throw new VaultParserException("An 'alias' declaration cannot declare a 'constructor' property, it must be handled on the parent declaration.");

        ServiceAlias def = new ServiceAlias(identifier, definitions.services().reference(target));
        Extractors.extractInvocations(json, def, definitions);
        Extractors.extractTags(json, definitions.tags());

        if (json.has("closeable"))
            throw new VaultParserException("The 'closeable' property cannot be used on an alias declaration.");

        return def;
    }
}
