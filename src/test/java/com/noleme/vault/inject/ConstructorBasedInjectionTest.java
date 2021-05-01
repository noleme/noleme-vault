package com.noleme.vault.inject;

import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;
import com.noleme.vault.service.DoubleProvider;
import com.noleme.vault.service.IntegerProvider;
import com.noleme.vault.service.StringProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/16
 */
public class ConstructorBasedInjectionTest
{
    @Test
    void typedInjection() throws VaultException
    {
        var vault = Vault.with("com/noleme/vault/parser/simple.yml");

        var service = vault.instance(MyTypedService.class);

        Assertions.assertEquals("SomeString", service.stringProvider.provide());
        Assertions.assertEquals(12.34, service.doubleProvider.provide());
        Assertions.assertEquals(2345, service.integerProvider.provide());
    }

    @Test
    void namedInjection() throws VaultException
    {
        var vault = Vault.with(
            "com/noleme/vault/parser/simple.yml",
            "com/noleme/vault/parser/provider.string.alt.yml"
        );

        var service = vault.instance(MyNamedService.class);

        Assertions.assertEquals("SomeString", service.stringProvider.provide());
        Assertions.assertEquals(12.34, service.doubleProvider.provide());
        Assertions.assertEquals(2345, service.integerProvider.provide());
    }

    @Test
    void mixedInjection() throws VaultException
    {
        var vault = Vault.with("com/noleme/vault/parser/simple.yml");

        var service = vault.instance(MyMixedService.class);

        Assertions.assertEquals("SomeString", service.stringProvider.provide());
        Assertions.assertEquals(12.34, service.doubleProvider.provide());
        Assertions.assertEquals(2345, service.integerProvider.provide());
    }

    @Test
    void lenientTypeInjection() throws VaultException
    {
        var vault = Vault.with("com/noleme/vault/parser/string_variable.yml");

        var service = vault.instance(MyLenientService.class);

        Assertions.assertEquals(1234, service.integerVal);
        Assertions.assertEquals(12.34F, service.floatVal);
        Assertions.assertEquals(12.34D, service.doubleVal);
        Assertions.assertFalse(service.booleanVal);
        Assertions.assertEquals((byte) 0x6c, service.byteVal);
        Assertions.assertEquals('c', service.charVal);
    }

    private static class MyTypedService
    {
        final StringProvider stringProvider;
        final DoubleProvider doubleProvider;
        final IntegerProvider integerProvider;

        @Inject
        private MyTypedService(StringProvider stringProvider, DoubleProvider doubleProvider, IntegerProvider integerProvider)
        {
            this.stringProvider = stringProvider;
            this.doubleProvider = doubleProvider;
            this.integerProvider = integerProvider;
        }
    }

    private static class MyNamedService
    {
        final StringProvider stringProvider;
        final DoubleProvider doubleProvider;
        final IntegerProvider integerProvider;

        @Inject
        private MyNamedService(
            @Named("provider.string") StringProvider stringProvider,
            @Named("provider.double") DoubleProvider doubleProvider,
            @Named("provider.integer") IntegerProvider integerProvider
        )
        {
            this.stringProvider = stringProvider;
            this.doubleProvider = doubleProvider;
            this.integerProvider = integerProvider;
        }
    }

    private static class MyMixedService
    {
        final StringProvider stringProvider;
        final DoubleProvider doubleProvider;
        @Inject public IntegerProvider integerProvider;

        @Inject
        private MyMixedService(StringProvider stringProvider, DoubleProvider doubleProvider)
        {
            this.stringProvider = stringProvider;
            this.doubleProvider = doubleProvider;
        }
    }

    private static class MyLenientService
    {
        final int integerVal;
        final float floatVal;
        final double doubleVal;
        final boolean booleanVal;
        final char charVal;
        final byte byteVal;

        @Inject
        private MyLenientService(
            @Named("integer_val.string") int integerVal,
            @Named("float_val.string") float floatVal,
            @Named("float_val.string") double doubleVal,
            @Named("boolean_val.string") boolean booleanVal,
            @Named("char_val.string") char charVal,
            @Named("byte_val.string") byte byteVal
        )
        {
            this.integerVal = integerVal;
            this.floatVal = floatVal;
            this.doubleVal = doubleVal;
            this.booleanVal = booleanVal;
            this.charVal = charVal;
            this.byteVal = byteVal;
        }
    }
}
