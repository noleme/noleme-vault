package com.noleme.vault.builder;

import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;
import com.noleme.vault.legacy.Key;
import com.noleme.vault.service.BooleanProvider;
import com.noleme.vault.service.IntegerProvider;
import com.noleme.vault.service.StringProvider;
import com.noleme.vault.service.annotated.AnnotatedProvider;
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
