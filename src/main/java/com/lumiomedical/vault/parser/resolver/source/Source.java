package com.lumiomedical.vault.parser.resolver.source;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lumiomedical.vault.exception.VaultResolverException;

import java.io.InputStream;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/23
 */
public class Source
{
    private final String origin;
    private final InputStream data;
    private final Dialect dialect;

    /**
     *
     * @param origin
     * @param data
     * @throws VaultResolverException
     */
    public Source(String origin, InputStream data) throws VaultResolverException
    {
        this(origin, data, Dialect.guess(origin));
    }

    /**
     *
     * @param origin
     * @param data
     * @param dialect
     */
    Source(String origin, InputStream data, Dialect dialect)
    {
        this.origin = origin;
        this.data = data;
        this.dialect = dialect;
    }

    public String getOrigin()
    {
        return this.origin;
    }

    public Dialect getDialect()
    {
        return this.dialect;
    }

    /**
     *
     * @return
     * @throws VaultResolverException
     */
    public ObjectNode interpret() throws VaultResolverException
    {
        return this.dialect.interpret(this.data);
    }
}
