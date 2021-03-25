package com.noleme.vault.parser.resolver.source;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.exception.VaultResolverException;
import com.noleme.vault.parser.resolver.dialect.Dialect;
import com.noleme.vault.parser.resolver.dialect.interpreter.DialectInterpreter;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/23
 */
public class Source <D>
{
    private final String origin;
    private final D data;
    private final DialectInterpreter<D> interpreter;

    /**
     *
     * @param origin
     * @param data
     * @throws VaultResolverException
     */
    public Source(String origin, D data) throws VaultResolverException
    {
        this(origin, data, Dialect.guess(origin, data));
    }

    /**
     *
     * @param origin
     * @param data
     * @param interpreter
     */
    public Source(String origin, D data, DialectInterpreter<D> interpreter)
    {
        this.origin = origin;
        this.data = data;
        this.interpreter = interpreter;
    }

    public String getOrigin()
    {
        return this.origin;
    }

    /**
     *
     * @return
     * @throws VaultResolverException
     */
    public ObjectNode interpret() throws VaultResolverException
    {
        return this.interpreter.interpret(this.data);
    }
}
