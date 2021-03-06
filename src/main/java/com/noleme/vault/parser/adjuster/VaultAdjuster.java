package com.noleme.vault.parser.adjuster;

import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultParserException;

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
