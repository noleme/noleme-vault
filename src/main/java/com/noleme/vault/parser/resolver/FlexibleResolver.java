package com.noleme.vault.parser.resolver;

import com.noleme.commons.file.Files;
import com.noleme.commons.file.Resources;
import com.noleme.vault.exception.VaultResolverException;
import com.noleme.vault.parser.resolver.source.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 21/10/2018
 */
@SuppressWarnings("rawtypes")
public class FlexibleResolver implements VaultResolver
{
    private static final Logger logger = LoggerFactory.getLogger(FlexibleResolver.class);

    @Override
    public Source resolve(String origin) throws VaultResolverException
    {
        try {
            logger.debug("Resolving from file/resource located at {}", origin);

            if (Files.fileExists(origin))
                return new Source<>(origin, Files.streamFrom(origin));
            return new Source<>(origin, Resources.streamFrom(origin));
        }
        catch (IOException e) {
            throw new VaultResolverException("The configuration file could not be loaded at "+origin+".", e);
        }
    }
}
