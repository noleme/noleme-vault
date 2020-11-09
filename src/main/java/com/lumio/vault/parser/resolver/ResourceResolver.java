package com.lumio.vault.parser.resolver;

import com.noleme.commons.file.Resources;
import com.lumio.vault.exception.VaultResolverException;
import com.lumio.vault.parser.resolver.source.Source;

import java.io.IOException;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 21/10/2018
 */
public class ResourceResolver implements VaultResolver
{
    @Override
    public Source resolve(String path) throws VaultResolverException
    {
        try {
            return new Source(path, Resources.streamFrom(path));
        }
        catch (IOException e) {
            throw new VaultResolverException("The configuration file could not be loaded at "+path+".", e);
        }
    }
}
