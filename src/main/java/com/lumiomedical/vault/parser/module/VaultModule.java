package com.lumiomedical.vault.parser.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.VaultParserException;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/24
 */
public interface VaultModule
{
    /**
     *
     * @return
     */
    String identifier();

    /**
     *
     * @param node
     * @param definitions
     * @throws VaultParserException
     */
    void process(JsonNode node, Definitions definitions) throws VaultParserException;
}
