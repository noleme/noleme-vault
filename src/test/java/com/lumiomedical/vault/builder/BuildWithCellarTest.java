package com.lumiomedical.vault.builder;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.factory.VaultFactory;
import com.lumiomedical.vault.service.annotated.AnnotatedProvider;
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
        Vault vault = Vault.with("com/lumiomedical/vault/parser/simple.yml");
        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-2345-false", provider.provide());
    }

    @Test
    void withCellar() throws VaultException
    {
        Cellar cellar = new VaultFactory().populate(new Cellar(), "com/lumiomedical/vault/parser/simple.yml");
        Vault vault = Vault.with(cellar);
        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-2345-false", provider.provide());
    }
}
