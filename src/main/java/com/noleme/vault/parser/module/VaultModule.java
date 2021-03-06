package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultParserException;

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
    void process(ObjectNode node, Definitions definitions) throws VaultParserException;
}
