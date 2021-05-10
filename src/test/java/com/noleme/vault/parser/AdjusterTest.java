package com.noleme.vault.parser;

import com.noleme.vault.container.definition.Definitions;
import com.noleme.vault.container.definition.ServiceProvider;
import com.noleme.vault.exception.VaultParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/28
 */
public class AdjusterTest
{
    @Test
    void testVariable() throws VaultParserException
    {
        var parser = new VaultCompositeParser();
        int value = 1234;

        Definitions def = parser.extract("com/noleme/vault/parser/simple.json", new Definitions(), vars -> {
            vars.set("my_variable", value);
        });

        Assertions.assertEquals(value, def.variables().get("my_variable"));
    }

    @Test
    void testResolving() throws VaultParserException
    {
        var parser = new VaultCompositeParser();
        var string = "this is an entirely new string";

        Definitions defA = parser.extract("com/noleme/vault/parser/simple.json");
        Assertions.assertEquals("SomeString", ((ServiceProvider)defA.services().get("provider.string")).getMethodArgs()[0]);

        Definitions defB = parser.extract("com/noleme/vault/parser/simple.json", new Definitions(), vars -> {
            vars.set("provider.string.base_value", string);
        });
        Assertions.assertEquals(string, ((ServiceProvider)defB.services().get("provider.string")).getMethodArgs()[0]);
    }

    @Test
    void testReplacement() throws VaultParserException
    {
        var parser = new VaultCompositeParser();
        var string = "this is an entirely new string";

        Definitions defA = parser.extract("com/noleme/vault/parser/simple.json");
        Assertions.assertEquals("SomeString", ((ServiceProvider)defA.services().get("provider.string")).getMethodArgs()[0]);

        Definitions defB = parser.extract("com/noleme/vault/parser/simple.json", new Definitions(), vars -> {
            vars.set("provider.string.value", string);
        });
        Assertions.assertEquals(string, ((ServiceProvider)defB.services().get("provider.string")).getMethodArgs()[0]);
    }
}
