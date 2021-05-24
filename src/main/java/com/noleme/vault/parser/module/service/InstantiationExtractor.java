package com.noleme.vault.parser.module.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.definition.ServiceDefinition;
import com.noleme.vault.container.definition.ServiceInstantiation;
import com.noleme.vault.exception.VaultParserException;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public class InstantiationExtractor implements ServiceDefinitionExtractor
{
    @Override
    public boolean accepts(ObjectNode json)
    {
        return json.has("class");
    }

    @Override
    public ServiceDefinition extract(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        String identifier = json.get("identifier").asText();
        String className = json.get("class").asText();

        ServiceInstantiation def = new ServiceInstantiation(identifier, className);
        Extractors.extractConstructor(json, def, definitions);
        Extractors.extractInvocations(json, def, definitions);
        Extractors.extractTags(json, definitions.tags());

        if (json.has("closeable") && json.get("closeable").asBoolean())
            def.setCloseable(true);

        return def;
    }
}
