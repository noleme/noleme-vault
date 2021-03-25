package com.noleme.vault.parser.resolver.dialect;

import com.noleme.vault.exception.VaultResolverException;
import com.noleme.vault.parser.resolver.dialect.interpreter.DialectInterpreter;
import com.noleme.vault.parser.resolver.dialect.interpreter.JsonInterpreter;
import com.noleme.vault.parser.resolver.dialect.interpreter.YamlInterpreter;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/23
 */
public final class Dialect
{
    public static final DialectInterpreter<InputStream> JSON = new JsonInterpreter();
    public static final DialectInterpreter<InputStream> YAML = new YamlInterpreter();

    private static final Map<Class<? extends DialectInterpreter>, DialectInterpreter> interpreters = new ConcurrentHashMap<>();

    static {
        register(JSON);
        register(YAML);
    }

    private Dialect() {}

    /**
     *
     * @param interpreter
     */
    public static void register(DialectInterpreter interpreter)
    {
        interpreters.put(interpreter.getClass(), interpreter);
    }

    /**
     *
     * @param origin
     * @param data
     * @param <D>
     * @return
     * @throws VaultResolverException
     */
    @SuppressWarnings("unchecked")
    public static <D> DialectInterpreter<D> guess(String origin, D data) throws VaultResolverException
    {
        for (DialectInterpreter interpreter : interpreters.values())
        {
            /* If the interpreter is able to handle it, we should be able to cast the interpreter */
            if (interpreter.canInterpret(data) && interpreter.isExpected(origin))
                return (DialectInterpreter<D>)interpreter;
        }
        throw new VaultResolverException("No dialect interpreter could be guessed for origin "+origin+" and data of type "+data.getClass().getName());
    }
}
