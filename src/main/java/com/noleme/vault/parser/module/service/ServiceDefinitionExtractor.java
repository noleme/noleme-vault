package com.noleme.vault.parser.module.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.definition.ServiceDefinition;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultParserException;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public interface ServiceDefinitionExtractor
{
    /**
     *
     * @param json
     * @return true if the extractor can handle this definition, false otherwise
     */
    boolean accepts(ObjectNode json);

    /**
     *
     * @param json
     * @param definitions
     * @return
     * @throws VaultParserException
     */
    ServiceDefinition extract(ObjectNode json, Definitions definitions) throws VaultParserException;
}
