package com.lumiomedical.vault.factory;

import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.exception.VaultInjectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public class FactoryTest
{
    @Test
    void basicJsonFactory() throws VaultInjectionException
    {
        var container = new VaultFactory().populate(new Cellar(), "com/lumiomedical/vault/parser/simple.json");

        makeAssertions(container);
    }

    @Test
    void basicYamlParsing() throws VaultInjectionException
    {
        var container = new VaultFactory().populate(new Cellar(), "com/lumiomedical/vault/parser/simple.yml");

        makeAssertions(container);
    }

    void makeAssertions(Cellar cellar)
    {
        Assertions.assertEquals(true, cellar.get("provider.boolean.value"));
        Assertions.assertEquals(12.34, cellar.get("provider.double.value"));
        Assertions.assertEquals("SomeString", cellar.get("provider.string.value"));
        Assertions.assertEquals(2345, cellar.get("provider.integer.value"));
    }
}
