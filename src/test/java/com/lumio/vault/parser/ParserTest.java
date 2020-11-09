package com.lumio.vault.parser;

import com.lumio.vault.container.definition.Definitions;
import com.lumio.vault.exception.VaultParserException;
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
        var parser = new VaultFlexibleParser();

        Definitions def = parser.extract("com/lumio/vault/parser/simple.json");

        makeAssertions(def);
    }

    @Test
    void basicYamlParsing() throws VaultParserException
    {
        var parser = new VaultFlexibleParser();

        Definitions def = parser.extract("com/lumio/vault/parser/simple.yml");

        makeAssertions(def);
    }

    void makeAssertions(Definitions def)
    {
        Assertions.assertEquals(true, def.getVariable("provider.boolean.value"));
        Assertions.assertEquals(12.34, def.getVariable("provider.double.value"));
        Assertions.assertEquals("SomeString", def.getVariable("provider.string.value"));
        Assertions.assertEquals(2345, def.getVariable("provider.integer.value"));
    }
}
