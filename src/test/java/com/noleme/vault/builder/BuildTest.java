package com.noleme.vault.builder;

import com.noleme.vault.Vault;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.exception.VaultException;
import com.noleme.vault.factory.VaultFactory;
import com.noleme.vault.legacy.Key;
import com.noleme.vault.service.BooleanProvider;
import com.noleme.vault.service.IntegerProvider;
import com.noleme.vault.service.annotated.AnnotatedProvider;
import com.noleme.vault.service.module.ProviderModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/13
 */
public class BuildTest
{
    @Test
    void withStage() throws VaultException
    {
        Vault vault = Vault.with(new ModuleStage(new ProviderModule()));
        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-2345-false", provider.provide());
    }

    @Test
    void withCombinationTest() throws VaultException
    {
        Vault vault = Vault.builder()
            .with("com/noleme/vault/parser/simple.yml")
            .with(new VaultFactory().populate(new Cellar(), "com/noleme/vault/parser/provider/provider.integer.json"))
            .with("com/noleme/vault/parser/provider/provider.integer.json")
            .with(Key.of(IntegerProvider.class, "provider.integer"), () -> new IntegerProvider(5432))
            .with(new ProviderModule())
            .with(Key.of(BooleanProvider.class, "provider.boolean"), () -> new BooleanProvider(true))
            .build()
        ;

        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-2345-true", provider.provide());
    }
}
