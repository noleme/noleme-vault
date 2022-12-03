package com.noleme.vault.parser.adjuster;

import com.noleme.vault.exception.VaultParserException;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 03/12/2022
 */
public interface Adjuster<T>
{
    void adjust(T input) throws VaultParserException;
}
