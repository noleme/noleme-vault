package com.lumio.vault;

import com.lumio.vault.container.Cellar;
import com.lumio.vault.exception.VaultException;
import com.lumio.vault.factory.VaultFactory;
import com.lumio.vault.service.annotated.AnnotatedProvider;
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
            .with("com/lumio/vault/parser/simple.yml")
            .with(new VaultFactory().populate(new Cellar(), "com/lumio/vault/parser/provider.integer.json"))
            .with("com/lumio/vault/parser/provider.integer.json")
            .build()
        ;

        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        System.out.println(provider.provide());
    }
}
