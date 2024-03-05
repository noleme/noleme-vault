package com.noleme.vault.inject;

import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;
import com.noleme.vault.service.DoubleProvider;
import com.noleme.vault.service.IntegerProvider;
import com.noleme.vault.service.StringProvider;
import com.noleme.vault.service.ValueProvider;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/13
 */
public class JavaxFieldBasedInjectionTest
{
    @Test
    void typeInjection() throws VaultException
    {
        var vault = Vault.with("com/noleme/vault/parser/simple.yml");

        var service = vault.inject(new MyTypedService());

        Assertions.assertEquals("SomeString", service.stringProvider.provide());
        Assertions.assertEquals(12.34, service.doubleProvider.provide());
        Assertions.assertEquals(2345, service.integerProvider.provide());
    }

    @Test
    void nameInjection() throws VaultException
    {
        var vault = Vault.with("com/noleme/vault/parser/simple.yml");

        var service = vault.inject(new MyNamedService());

        Assertions.assertEquals("SomeString", service.stringProvider.provide());
        Assertions.assertEquals(12.34, service.doubleProvider.provide());
        Assertions.assertEquals(2345, service.integerProvider.provide());
    }

    @Test
    void lenientTypeInjection() throws VaultException
    {
        var vault = Vault.with("com/noleme/vault/parser/variable/string_variable.yml");

        var service = vault.inject(new MyLenientService());

        Assertions.assertEquals(1234, service.integerVal);
        Assertions.assertEquals(1234, service.integerPrimitive);
        Assertions.assertEquals(12.34F, service.floatVal);
        Assertions.assertEquals(12.34F, service.floatPrimitive);
        Assertions.assertEquals(12.34D, service.doubleVal);
        Assertions.assertEquals(12.34D, service.doublePrimitive);
        Assertions.assertEquals(false, service.booleanVal);
        Assertions.assertFalse(service.booleanPrimitive);
        Assertions.assertEquals((byte) 0x6c, service.byteVal);
        Assertions.assertEquals((byte) 0x6c, service.bytePrimitive);
        Assertions.assertEquals('c', service.charVal);
        Assertions.assertEquals('c', service.charPrimitive);
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

    private static class MyLenientService
    {
        @Inject @Named("integer_val.string") Integer integerVal;
        @Inject @Named("integer_val.string") int integerPrimitive;
        @Inject @Named("float_val.string") Float floatVal;
        @Inject @Named("float_val.string") float floatPrimitive;
        @Inject @Named("float_val.string") Double doubleVal;
        @Inject @Named("float_val.string") double doublePrimitive;
        @Inject @Named("boolean_val.string") Boolean booleanVal;
        @Inject @Named("boolean_val.string") boolean booleanPrimitive;
        @Inject @Named("char_val.string") Character charVal;
        @Inject @Named("char_val.string") char charPrimitive;
        @Inject @Named("byte_val.string") Byte byteVal;
        @Inject @Named("byte_val.string") byte bytePrimitive;
    }
}
