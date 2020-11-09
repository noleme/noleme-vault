package com.lumiomedical.vault.parser.resolver.source;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.json.Json;
import com.noleme.json.JsonException;
import com.noleme.json.Yaml;
import com.lumiomedical.vault.exception.VaultResolverException;

import java.io.InputStream;
import java.util.function.Predicate;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/23
 */
public enum Dialect
{
    JSON(
        path -> path.endsWith(".json"),
        is -> {
            try {
                return (ObjectNode) Json.parse(is);
            }
            catch (JsonException e) {
                throw new VaultResolverException("An error occurred while attempting to interpret the source as JSON.", e);
            }
        }
    ),
    YAML(
        path -> path.endsWith(".yaml") || path.endsWith(".yml"),
        is -> {
            try {
                return (ObjectNode) Yaml.parse(is);
            }
            catch (JsonException e) {
                throw new VaultResolverException("An error occurred while attempting to interpret the source as YAML.", e);
            }
        }
    );

    private final Predicate<String> predicate;
    private final DialectInterpreter interpreter;

    /**
     *
     * @param predicate
     * @param interpreter
     */
    Dialect(Predicate<String> predicate, DialectInterpreter interpreter)
    {
        this.predicate = predicate;
        this.interpreter = interpreter;
    }

    /**
     *
     * @param data
     * @return
     */
    public ObjectNode interpret(InputStream data) throws VaultResolverException
    {
        return this.interpreter.interpret(data);
    }

    /**
     *
     * @param path
     * @return
     * @throws VaultResolverException
     */
    public static Dialect guess(String path) throws VaultResolverException
    {
        for (Dialect dialect : Dialect.values())
        {
            if (dialect.predicate.test(path))
                return dialect;
        }
        throw new VaultResolverException("No dialect interpreter could be guessed for path string "+path);
    }
}
