package com.lumiomedical.vault.builder;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.service.StringProvider;
import com.lumiomedical.vault.service.annotated.AnnotatedProvider;
import com.lumiomedical.vault.service.module.ProviderModule;
import com.lumiomedical.vault.service.module.SingletonProviderModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/13
 */
public class BuildWithModuleTest
{
    @Test
    void withModule() throws VaultException
    {
        Vault vault = Vault.with(new ProviderModule());
        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-2345-false", provider.provide());

        StringProvider provider1 = vault.instance(StringProvider.class, "provider.string");
        StringProvider provider2 = vault.instance(StringProvider.class, "provider.string");

        Assertions.assertNotSame(provider1, provider2);
    }

    @Test
    void withSingletonModule() throws VaultException
    {
        Vault vault = Vault.with(new SingletonProviderModule());
        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-2345-false", provider.provide());

        StringProvider provider1 = vault.instance(StringProvider.class, "provider.string");
        StringProvider provider2 = vault.instance(StringProvider.class, "provider.string");

        Assertions.assertSame(provider1, provider2);
    }
}
