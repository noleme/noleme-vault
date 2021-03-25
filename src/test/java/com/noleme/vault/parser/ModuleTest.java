package com.noleme.vault.parser;

import com.noleme.vault.container.Cellar;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.factory.VaultFactory;
import com.noleme.vault.parser.module.CustomModule;
import com.noleme.vault.service.StringProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/24
 */
public class ModuleTest
{
    @Test
    void test() throws VaultInjectionException
    {
        VaultFactory.defaultParser.register(new CustomModule());
        var container = new VaultFactory().populate(new Cellar(), "com/noleme/vault/parser/module.yml");

        Assertions.assertEquals("this_is_my_new_string", container.getService("my_provider.a", StringProvider.class).provide());
        Assertions.assertEquals("this_is_my_new_string", container.getService("my_provider.b", StringProvider.class).provide());
        Assertions.assertEquals("this_is_my_new_string", container.getService("my_provider.c", StringProvider.class).provide());
    }
}
