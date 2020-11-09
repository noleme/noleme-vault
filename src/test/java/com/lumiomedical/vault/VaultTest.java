package com.lumiomedical.vault;

import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.factory.VaultFactory;
import com.lumiomedical.vault.service.annotated.AnnotatedProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public class VaultTest
{
    @Test
    void withContainerTest() throws VaultException
    {
        Vault vault = Vault.builder()
            .with("com/lumiomedical/vault/parser/simple.yml")
            .with(new VaultFactory().populate(new Cellar(), "com/lumiomedical/vault/parser/provider.integer.json"))
            .with("com/lumiomedical/vault/parser/provider.integer.json")
            .build()
        ;

        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-1234-false", provider.provide());
    }
}
