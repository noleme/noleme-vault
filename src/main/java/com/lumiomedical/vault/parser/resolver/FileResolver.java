package com.lumiomedical.vault.parser.resolver;

import com.noleme.commons.file.Files;
import com.lumiomedical.vault.exception.VaultResolverException;
import com.lumiomedical.vault.parser.resolver.source.Source;

import java.io.FileNotFoundException;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 21/10/2018
 */
public class FileResolver implements VaultResolver
{
    @Override
    public Source resolve(String path) throws VaultResolverException
    {
        try {
            return new Source(path, Files.streamFrom(path));
        }
        catch (FileNotFoundException e) {
            throw new VaultResolverException("The configuration file could not be loaded at "+path+".", e);
        }
    }
}
