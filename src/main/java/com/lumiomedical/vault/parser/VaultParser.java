package com.lumiomedical.vault.parser;

import com.lumiomedical.vault.exception.VaultParserException;
import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.parser.module.VaultModule;
import com.lumiomedical.vault.parser.resolver.source.Source;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 05/02/15.
 */
public interface VaultParser
{
    /**
     *
     * @param path
     * @return
     * @throws VaultParserException
     */
    default Definitions extract(String path) throws VaultParserException
    {
        return this.extract(path, new Definitions());
    }

    /**
     *
     * @param path
     * @param definitions
     * @return
     * @throws VaultParserException
     */
    Definitions extract(String path, Definitions definitions) throws VaultParserException;

    /**
     *
     * @param source
     * @return
     * @throws VaultParserException
     */
    default Definitions extract(Source source) throws VaultParserException
    {
        return this.extract(source, new Definitions());
    }

    /**
     *
     * @param source
     * @param definitions
     * @return
     * @throws VaultParserException
     */
    Definitions extract(Source source, Definitions definitions) throws VaultParserException;

    /**
     *
     * @param module
     * @return
     */
    VaultParser register(VaultModule module);
}
