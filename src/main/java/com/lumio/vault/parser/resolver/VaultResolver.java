package com.lumio.vault.parser.resolver;

import com.lumio.vault.exception.VaultResolverException;
import com.lumio.vault.parser.resolver.source.Source;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 21/10/2018
 */
public interface VaultResolver
{
    /**
     *
     * @param path
     * @return
     * @throws VaultResolverException
     */
    Source resolve(String path) throws VaultResolverException;
}
