package com.lumiomedical.vault.parser.resolver.dialect.interpreter;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lumiomedical.vault.exception.VaultResolverException;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/07/06
 */
public interface DialectInterpreter <D>
{
    /**
     *
     * @param origin
     * @return
     */
    boolean isExpected(String origin);

    /**
     *
     * @param data
     * @return
     */
    boolean canInterpret(Object data);

    /**
     *
     * @param data
     * @return
     * @throws VaultResolverException
     */
    ObjectNode interpret(D data) throws VaultResolverException;
}
