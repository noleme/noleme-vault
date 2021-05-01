package com.noleme.vault.parser.resolver;

import com.noleme.vault.exception.VaultResolverException;
import com.noleme.vault.parser.resolver.source.Source;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 21/10/2018
 */
@SuppressWarnings("rawtypes")
public interface VaultResolver
{
    /**
     *
     * @param origin
     * @return
     * @throws VaultResolverException
     */
    Source resolve(String origin) throws VaultResolverException;
}
