package com.lumiomedical.vault.parser;

import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.VaultParserException;
import com.lumiomedical.vault.parser.resolver.source.Dialect;
import com.lumiomedical.vault.parser.resolver.source.RawSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/07/06
 */
public class RawSourceTest
{
    @Test
    void jsonRawTest() throws VaultParserException
    {
        var parser = new VaultFlexibleParser();
        var source = new RawSource("{" +
            "\"variables\":{" +
                "\"provider.double.value\":12.34," +
                "\"provider.string.value\":\"SomeString\"," +
                "\"provider.boolean.value\":true," +
                "\"provider.integer.value\":2345" +
            "}" +
        "}", Dialect.JSON);

        Definitions def = parser.extract(source);

        makeAssertions(def);
    }

    @Test
    void yamlRawTest() throws VaultParserException
    {
        var parser = new VaultFlexibleParser();
        var source = new RawSource("variables:\n" +
            "  provider.double.value: 12.34\n" +
            "  provider.string.value: SomeString\n" +
            "  provider.boolean.value: true\n" +
            "  provider.integer.value: 2345\n"
        , Dialect.YAML);

        Definitions def = parser.extract(source);

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
