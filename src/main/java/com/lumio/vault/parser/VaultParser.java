package com.lumio.vault.parser;

import com.lumio.vault.exception.VaultParserException;
import com.lumio.vault.container.definition.Definitions;
import com.lumio.vault.parser.resolver.source.Source;

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
}
