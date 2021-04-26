package com.noleme.vault.reflect;

import com.noleme.commons.container.Lists;
import com.noleme.vault.exception.VaultInvalidTypeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: this needs many more tests in order to cover a variety of edge-cases (null values, complex overloading/override setups, etc.)
 *
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 27/04/2021
 */
public class LenientClassUtilsTest
{
    @Test
    @SuppressWarnings("unchecked")
    void constructorLookup()
    {

        List<String> list = List.of("a", "b", "c");

        Assertions.assertDoesNotThrow(() -> {
            var ctor = ClassUtils.getConstructor(ArrayList.class, new Class[]{list.getClass()});

            List<String> myList = (List<String>) ctor.newInstance(list);

            Assertions.assertIterableEquals(list, myList);
        });
    }

    @Test
    void methodLookup()
    {
        List<String> list = Lists.of("a", "b", "c");

        Assertions.assertDoesNotThrow(() -> {
            var method = ClassUtils.getMethod(list.getClass(), "get", new Class[]{ int.class });
            Assertions.assertEquals(list.get(2), method.invoke(list, 2));
        });

        Assertions.assertDoesNotThrow(() -> {
            var method = ClassUtils.getMethod(list.getClass(), "get", new Class[]{ Integer.class });
            Assertions.assertEquals(list.get(2), method.invoke(list, 2));
        });

        Assertions.assertThrows(NoSuchMethodException.class, () -> {
            ClassUtils.getMethod(list.getClass(), "get", new Class[]{ Long.class });
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
}
