package com.noleme.vault.reflect;

import com.noleme.vault.exception.VaultInvalidTypeException;
import com.noleme.vault.service.BooleanProvider;
import com.noleme.vault.service.DoubleProvider;
import com.noleme.vault.service.IntegerProvider;
import com.noleme.vault.service.StringProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * TODO: this needs many more tests in order to cover a variety of edge-cases (null values, complex overloading/override setups, etc.)
 *
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 27/04/2021
 */
public class LenientClassUtilsTest
{
    @Test
    void constructorLookup()
    {
        Assertions.assertThrows(NoSuchMethodException.class, () -> {
            LenientClassUtils.getLenientConstructor(IntegerProvider.class, classes(int.class), args("abc"));
        });

        Assertions.assertDoesNotThrow(() -> {
            LenientClassUtils.getLenientConstructor(StringProvider.class, classes(String.class), args("something"));
            LenientClassUtils.getLenientConstructor(IntegerProvider.class, classes(String.class), args("123"));
            LenientClassUtils.getLenientConstructor(BooleanProvider.class, classes(String.class), args("false"));
            LenientClassUtils.getLenientConstructor(DoubleProvider.class, classes(String.class), args("12.34"));
        });

        Assertions.assertDoesNotThrow(() -> {
            LenientClassUtils.getLenientConstructor(TrapClass.class, classes(String.class), args("my_string"));
            LenientClassUtils.getLenientConstructor(TrapClass.class, classes(String.class, String.class), args("my_string", "123"));
        });
        Assertions.assertThrows(NoSuchMethodException.class, () -> {
            LenientClassUtils.getLenientConstructor(TrapClass.class, classes(String.class, String.class), args("123", "my_string"));
        });

        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertEquals(
                ClassUtils.getConstructor(TrapClass.class, classes(String.class, Integer.class)),
                LenientClassUtils.getLenientConstructor(TrapClass.class, classes(String.class, String.class), args("my_string", "123")).first
            );
        });
    }

    @Test
    void methodLookup()
    {
        Assertions.assertDoesNotThrow(() -> {
            LenientClassUtils.getLenientMethod(TrapClass.class, "method", classes(String.class), args("my_string"));
            LenientClassUtils.getLenientMethod(TrapClass.class, "method", classes(String.class, String.class), args("my_string", "123"));
        });
        Assertions.assertThrows(NoSuchMethodException.class, () -> {
            LenientClassUtils.getLenientMethod(TrapClass.class, "method", classes(String.class, String.class), args("123", "my_string"));
        });

        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertEquals(
                ClassUtils.getMethod(TrapClass.class, "method", classes(String.class, Integer.class)),
                LenientClassUtils.getLenientMethod(TrapClass.class, "method", classes(String.class, String.class), args("my_string", "123")).first
            );
        });
    }

    @Test
    void typeConversion()
    {
        Assertions.assertEquals("my_string", LenientClassUtils.attemptTypeConversion("my_string", String.class));

        Assertions.assertEquals(true, LenientClassUtils.attemptTypeConversion("true", boolean.class));
        Assertions.assertEquals(true, LenientClassUtils.attemptTypeConversion("true", Boolean.class));
        Assertions.assertEquals(false, LenientClassUtils.attemptTypeConversion("false", boolean.class));
        Assertions.assertEquals(false, LenientClassUtils.attemptTypeConversion("false", Boolean.class));
        Assertions.assertThrows(VaultInvalidTypeException.class, () -> LenientClassUtils.attemptTypeConversion("anything", boolean.class));

        Assertions.assertEquals(123, LenientClassUtils.attemptTypeConversion("123", int.class));
        Assertions.assertEquals(123, LenientClassUtils.attemptTypeConversion("123", Integer.class));
        Assertions.assertThrows(VaultInvalidTypeException.class, () -> LenientClassUtils.attemptTypeConversion("anything", int.class));

        Assertions.assertEquals(123L, LenientClassUtils.attemptTypeConversion("123", long.class));
        Assertions.assertEquals(123L, LenientClassUtils.attemptTypeConversion("123", Long.class));
        Assertions.assertThrows(VaultInvalidTypeException.class, () -> LenientClassUtils.attemptTypeConversion("anything", long.class));

        Assertions.assertEquals(12.34D, LenientClassUtils.attemptTypeConversion("12.34", double.class));
        Assertions.assertEquals(12.34D, LenientClassUtils.attemptTypeConversion("12.34", Double.class));
        Assertions.assertThrows(VaultInvalidTypeException.class, () -> LenientClassUtils.attemptTypeConversion("anything", double.class));

        Assertions.assertEquals(12.34F, LenientClassUtils.attemptTypeConversion("12.34", float.class));
        Assertions.assertEquals(12.34F, LenientClassUtils.attemptTypeConversion("12.34", Float.class));
        Assertions.assertThrows(VaultInvalidTypeException.class, () -> LenientClassUtils.attemptTypeConversion("anything", float.class));

        Assertions.assertEquals((byte) 0x6c, LenientClassUtils.attemptTypeConversion("0x6c", byte.class));
        Assertions.assertEquals((byte) 0x6c, LenientClassUtils.attemptTypeConversion("0x6c", Byte.class));
        Assertions.assertThrows(VaultInvalidTypeException.class, () -> LenientClassUtils.attemptTypeConversion("0x6ca", byte.class));
        Assertions.assertThrows(VaultInvalidTypeException.class, () -> LenientClassUtils.attemptTypeConversion("anything", byte.class));

        Assertions.assertEquals((short) 123, LenientClassUtils.attemptTypeConversion("123", short.class));
        Assertions.assertEquals((short) 123, LenientClassUtils.attemptTypeConversion("123", Short.class));
        Assertions.assertThrows(VaultInvalidTypeException.class, () -> LenientClassUtils.attemptTypeConversion("anything", short.class));

        Assertions.assertEquals('a', LenientClassUtils.attemptTypeConversion("a", char.class));
        Assertions.assertEquals('a', LenientClassUtils.attemptTypeConversion("a", Character.class));
        Assertions.assertEquals('b', LenientClassUtils.attemptTypeConversion("b", char.class));
        Assertions.assertEquals('\0', LenientClassUtils.attemptTypeConversion("\0", char.class));
        Assertions.assertThrows(VaultInvalidTypeException.class, () -> LenientClassUtils.attemptTypeConversion("", char.class));
        Assertions.assertThrows(VaultInvalidTypeException.class, () -> LenientClassUtils.attemptTypeConversion("anything", char.class));
    }

    private static Class<?>[] classes(Class<?>... classes)
    {
        return classes;
    }

    private static Object[] args(Object... args)
    {
        return args;
    }

    public static class TrapClass
    {
        public TrapClass(String arg) {}
        public TrapClass(Integer arg) {}
        public TrapClass(String arg1, Integer arg2) {}

        public void method(String arg) {}
        public void method(String arg, Integer arg2) {}
    }
}
