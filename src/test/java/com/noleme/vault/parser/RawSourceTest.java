package com.noleme.vault.parser;

import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.vault.parser.resolver.dialect.Dialect;
import com.noleme.vault.parser.resolver.source.RawSource;
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
        var parser = new VaultCompositeParser();
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
        var parser = new VaultCompositeParser();
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
        var vars = def.variables();

        Assertions.assertEquals(true, vars.get("provider.boolean.value"));
        Assertions.assertEquals(12.34, vars.get("provider.double.value"));
        Assertions.assertEquals("SomeString", vars.get("provider.string.value"));
        Assertions.assertEquals(2345, vars.get("provider.integer.value"));
    }
}
