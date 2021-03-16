package com.lumiomedical.vault.builder;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.factory.VaultFactory;
import com.lumiomedical.vault.legacy.Key;
import com.lumiomedical.vault.service.BooleanProvider;
import com.lumiomedical.vault.service.IntegerProvider;
import com.lumiomedical.vault.service.annotated.AnnotatedProvider;
import com.lumiomedical.vault.service.module.ProviderModule;
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
            .with("com/lumiomedical/vault/parser/simple.yml")
            .with(new VaultFactory().populate(new Cellar(), "com/lumiomedical/vault/parser/provider.integer.json"))
            .with("com/lumiomedical/vault/parser/provider.integer.json")
            .with(Key.of(IntegerProvider.class, "provider.integer"), () -> new IntegerProvider(5432))
            .with(new ProviderModule())
            .with(Key.of(BooleanProvider.class, "provider.boolean"), () -> new BooleanProvider(true))
            .build()
        ;

        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-2345-true", provider.provide());
    }
}
