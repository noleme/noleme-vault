package com.noleme.vault.parser;

import com.noleme.vault.container.definition.Definitions;
import com.noleme.vault.exception.VaultParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public class ParserTest
{
    @Test
    void basicJsonParsing() throws VaultParserException
    {
        var parser = new VaultCompositeParser();

        Definitions def = parser.extract("com/noleme/vault/parser/simple.json");

        makeAssertions(def);
    }

    @Test
    void basicYamlParsing() throws VaultParserException
    {
        var parser = new VaultCompositeParser();

        Definitions def = parser.extract("com/noleme/vault/parser/simple.yml");

        makeAssertions(def);
    }

    public static void makeAssertions(Definitions def)
    {
        var vars = def.getVariables();

        Assertions.assertEquals(true, vars.get("provider.boolean.value"));
        Assertions.assertEquals(12.34, vars.get("provider.double.value"));
        Assertions.assertEquals("SomeString", vars.get("provider.string.value"));
        Assertions.assertEquals(2345, vars.get("provider.integer.value"));
    }
}
