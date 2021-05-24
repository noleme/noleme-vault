package com.noleme.vault.parser.module.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.definition.ServiceDefinition;
import com.noleme.vault.container.definition.ServiceScopedImport;
import com.noleme.vault.container.definition.Tag;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.register.ScopedDefinitions;
import com.noleme.vault.container.register.index.Reference;
import com.noleme.vault.exception.VaultParserException;

import java.util.Set;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public class ScopedImportExtractor implements ServiceDefinitionExtractor
{
    @Override
    public boolean accepts(ObjectNode json)
    {
        return json.has("use");
    }

    @Override
    public ServiceDefinition extract(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        String identifier = json.get("identifier").asText();
        String use = json.get("use").asText();
        String from = json.has("from") ? json.get("from").asText() : null;

        int shorthandTagIndex = use.indexOf('@');
        if (shorthandTagIndex > -1)
        {
            String shortHandFrom = use.substring(shorthandTagIndex + 1);
            if (from != null && !shortHandFrom.equals(from))
                throw new VaultParserException("A shorthand notation "+use+" for scoped service import "+identifier+" conflicts with the provided from value "+from);

            from = shortHandFrom;
            use = use.substring(0, shorthandTagIndex);
        }

        ScopedDefinitions scope = definitions.scopes().get(from);
        if (scope == null)
            throw new VaultParserException("Service "+identifier+" makes a reference to an undeclared "+from+" scope.");

        String expectedIdentifier = scope.uid() + "#" + use;
        if (!scope.services().has(expectedIdentifier))
            throw new VaultParserException("Service "+identifier+" makes a reference to a non-existing "+use+" service in scope "+from);

        Reference ref = scope.services().reference(expectedIdentifier);

        ServiceScopedImport def = new ServiceScopedImport(identifier, from, ref);
        Extractors.extractInvocations(json, def, definitions);
        Extractors.extractTags(json, definitions.tags());

        portTagsFromScope(definitions, scope, identifier, use);

        return def;
    }

    /**
     *
     * @param definitions
     * @param scope
     * @param extractedIdentifier
     * @param scopedIdentifier
     */
    private static void portTagsFromScope(Definitions definitions, ScopedDefinitions scope, String extractedIdentifier, String scopedIdentifier)
    {
        Set<Tag> tags = scope.tags().forService(scopedIdentifier);
        if (tags == null)
            return;

        for (Tag tag : tags)
        {
            var portedTag = new Tag(
                tag.getIdentifier(),
                extractedIdentifier,
                tag.getNode()
            );

            definitions.tags().register(portedTag);
        }
    }
}
