package com.noleme.vault.parser.adjuster;

import com.noleme.vault.container.register.index.Variables;
import com.noleme.vault.exception.VaultParserException;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/28
 */
public interface VaultAdjuster
{
    /**
     *
     * @param variables
     * @throws VaultParserException
     */
    void adjust(Variables variables) throws VaultParserException;
}
