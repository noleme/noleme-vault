package com.noleme.vault.parser.resolver;

import com.noleme.vault.exception.VaultResolverException;
import com.noleme.vault.parser.resolver.source.Source;
import com.noleme.commons.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 21/10/2018
 */
@SuppressWarnings("rawtypes")
public class FileResolver implements VaultResolver
{
    private static final Logger logger = LoggerFactory.getLogger(FileResolver.class);

    @Override
    public Source resolve(String origin) throws VaultResolverException
    {
        try {
            logger.debug("Resolving from file located at {}", origin);

            return new Source<>(origin, Files.streamFrom(origin));
        }
        catch (FileNotFoundException e) {
            throw new VaultResolverException("The configuration file could not be loaded at "+origin+".", e);
        }
    }
}
