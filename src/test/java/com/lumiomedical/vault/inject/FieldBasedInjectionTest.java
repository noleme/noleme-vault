package com.lumiomedical.vault.inject;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.service.DoubleProvider;
import com.lumiomedical.vault.service.IntegerProvider;
import com.lumiomedical.vault.service.StringProvider;
import com.lumiomedical.vault.service.ValueProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/13
 */
public class FieldBasedInjectionTest
{
    @Test
    void typeInjection() throws VaultException
    {
        var vault = Vault.with("com/lumiomedical/vault/parser/simple.yml");

        var service = vault.inject(new MyTypedService());

        Assertions.assertEquals("SomeString", service.stringProvider.provide());
        Assertions.assertEquals(12.34, service.doubleProvider.provide());
        Assertions.assertEquals(2345, service.integerProvider.provide());
    }

    @Test
    void nameInjection() throws VaultException
    {
        var vault = Vault.with("com/lumiomedical/vault/parser/simple.yml");

        var service = vault.inject(new MyNamedService());

        Assertions.assertEquals("SomeString", service.stringProvider.provide());
        Assertions.assertEquals(12.34, service.doubleProvider.provide());
        Assertions.assertEquals(2345, service.integerProvider.provide());
    }

    private static class MyTypedService
    {
        @Inject StringProvider stringProvider;
        @Inject DoubleProvider doubleProvider;
        @Inject IntegerProvider integerProvider;
    }

    private static class MyNamedService
    {
        @Inject @Named("provider.string") ValueProvider<String> stringProvider;
        @Inject @Named("provider.double") ValueProvider<Double> doubleProvider;
        @Inject @Named("provider.integer") ValueProvider<Integer> integerProvider;
    }
}
