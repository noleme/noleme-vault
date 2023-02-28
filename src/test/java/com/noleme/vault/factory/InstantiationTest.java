package com.noleme.vault.factory;

import com.noleme.vault.Vault;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.container.definition.ServiceInstantiation;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.service.EnumProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.noleme.vault.parser.adjuster.VaultAdjuster.variables;

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

    @Test @SuppressWarnings("resource")
    void instantiate__withEnumArgument()
    {
        var vault = Assertions.assertDoesNotThrow(() -> Vault.with("com/noleme/vault/parser/provider/provider.enum.yml"));
        Assertions.assertEquals(EnumProvider.TestEnum.B, vault.instance(EnumProvider.class).provide());

        Assertions.assertThrows(VaultInjectionException.class, () -> Vault.with(
            "com/noleme/vault/parser/provider/provider.enum.yml",
            variables(vars -> vars.set("provider.enum.value", "D"))
        ));
    }

    public static class InnerClass
    {

    }
}
