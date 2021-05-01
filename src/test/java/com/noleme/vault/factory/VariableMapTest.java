package com.noleme.vault.factory;

import com.noleme.vault.container.Cellar;
import com.noleme.vault.exception.VaultInjectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 01/05/2021
 */
public class VariableMapTest
{
    private static final VaultFactory factory = new VaultFactory();

    @Test
    void mapVariableTest() throws VaultInjectionException
    {
        var container = factory.populate(new Cellar(), "com/noleme/vault/parser/map_variable.yml");

        Assertions.assertTrue()
    }

    void makeAssertions(Cellar cellar)
    {
        Assertions.assertEquals(true, cellar.get("provider.boolean.value"));
        Assertions.assertEquals(12.34, cellar.get("provider.double.value"));
        Assertions.assertEquals("SomeString", cellar.get("provider.string.value"));
        Assertions.assertEquals(2345, cellar.get("provider.integer.value"));
    }
}
