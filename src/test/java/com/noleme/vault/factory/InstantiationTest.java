package com.noleme.vault.factory;

import com.noleme.vault.container.Cellar;
import com.noleme.vault.container.definition.ServiceInstantiation;
import com.noleme.vault.container.register.Definitions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 25/07/2021
 */
public class InstantiationTest
{
    @Test
    void instantiateInnerClass()
    {
        var factory = new VaultFactory();
        var defs = new Definitions();

        defs.services().set("inner_class", new ServiceInstantiation("inner_class", "com.noleme.vault.factory.InstantiationTest$InnerClass"));

        Assertions.assertDoesNotThrow(() -> {
            factory.populate(new Cellar(), defs);
        });
    }

    public static class InnerClass
    {

    }
}
