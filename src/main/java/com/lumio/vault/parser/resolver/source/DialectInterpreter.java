package com.lumio.vault.parser.resolver.source;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lumio.vault.exception.VaultResolverException;

import java.io.InputStream;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/07/06
 */
public interface DialectInterpreter
{
    /**
     *
     * @param is
     * @return
     * @throws VaultResolverException
     */
    ObjectNode interpret(InputStream is) throws VaultResolverException;
}
