package com.noleme.vault.builder;

import com.noleme.vault.Vault;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.exception.VaultException;
import com.noleme.vault.factory.VaultFactory;
import com.noleme.vault.service.annotated.AnnotatedProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/13
 */
public class BuildWithCellarTest
{
    @Test
    void withPath() throws VaultException
    {
        Vault vault = Vault.with("com/noleme/vault/parser/simple.yml");
        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-2345-false", provider.provide());
    }

    @Test
    void withCellar() throws VaultException
    {
        Cellar cellar = new VaultFactory().populate(new Cellar(), "com/noleme/vault/parser/simple.yml");
        Vault vault = Vault.with(cellar);
        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-2345-false", provider.provide());
    }
}
