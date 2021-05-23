package com.noleme.vault.parser.module.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.definition.ServiceDefinition;
import com.noleme.vault.container.definition.ServiceProvider;
import com.noleme.vault.exception.VaultParserException;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public class MethodProviderExtractor implements ServiceDefinitionExtractor
{
    @Override
    public boolean accepts(ObjectNode json)
    {
        return json.has("method");
    }

    @Override
    public ServiceDefinition extract(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        String identifier = json.get("identifier").asText();
        String className = json.get("class").asText();
        String methodName = json.get("method").asText();

        ServiceProvider def = new ServiceProvider(identifier, className, methodName);
        Extractors.extractMethod(json, def, definitions);
        Extractors.extractInvocations(json, def, definitions);
        Extractors.extractTags(json, definitions.tags());

        if (json.has("closeable") && json.get("closeable").asBoolean())
            def.setCloseable(true);

        return def;
    }
}
