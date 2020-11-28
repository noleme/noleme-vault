package com.lumiomedical.vault.parser;

import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.exception.VaultInjectionException;
import com.lumiomedical.vault.factory.VaultFactory;
import com.lumiomedical.vault.parser.module.CustomModule;
import com.lumiomedical.vault.service.StringProvider;
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
        var container = new VaultFactory().populate(new Cellar(), "com/lumiomedical/vault/parser/module.yml");

        Assertions.assertEquals("this_is_my_new_string", container.getService("my_provider.a", StringProvider.class).provide());
        Assertions.assertEquals("this_is_my_new_string", container.getService("my_provider.b", StringProvider.class).provide());
        Assertions.assertEquals("this_is_my_new_string", container.getService("my_provider.c", StringProvider.class).provide());
    }
}
