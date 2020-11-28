package com.lumiomedical.vault.parser.adjuster;

import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.VaultParserException;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/28
 */
public interface VaultAdjuster
{
    /**
     *
     * @param definitions
     * @throws VaultParserException
     */
    void adjust(Definitions definitions) throws VaultParserException;
}
