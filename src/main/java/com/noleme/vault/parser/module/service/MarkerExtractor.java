package com.noleme.vault.parser.module.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.definition.ServiceDefinition;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultParserException;

/**
 * Marker definitions rely on short-hand string notations.
 * As it stands, no feature relies on markers ; tag declarations did for a short lapse, but were moved to a separate module.
 *
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public class MarkerExtractor implements ServiceDefinitionExtractor
{
    @Override
    public boolean accepts(ObjectNode json)
    {
        return json.has("marker");
    }

    @Override
    public ServiceDefinition extract(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        String marker = json.get("marker").asText();

        throw new VaultParserException("An unknown marker of type "+marker+" was found.");
    }
}
