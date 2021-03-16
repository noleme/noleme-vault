package com.lumiomedical.vault.builder;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.legacy.Key;
import com.lumiomedical.vault.service.BooleanProvider;
import com.lumiomedical.vault.service.IntegerProvider;
import com.lumiomedical.vault.service.StringProvider;
import com.lumiomedical.vault.service.annotated.AnnotatedProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/13
 */
public class BuildWithProviderTest
{
    @Test
    void withProvider() throws VaultException
    {
        Vault vault = Vault.builder()
            .with(Key.of(StringProvider.class, "provider.string"), () -> new StringProvider("SomeString"))
            .with(Key.of(IntegerProvider.class, "provider.integer"), () -> new IntegerProvider(2345))
            .with(Key.of(BooleanProvider.class, "provider.boolean"), () -> new BooleanProvider(false))
            .build()
        ;
        AnnotatedProvider provider = vault.inject(new AnnotatedProvider());

        Assertions.assertEquals("SomeString-2345-false", provider.provide());
    }
}
