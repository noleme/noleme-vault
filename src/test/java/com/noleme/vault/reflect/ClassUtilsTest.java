package com.noleme.vault.reflect;

import com.noleme.commons.container.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: this needs many more tests in order to cover a variety of edge-cases (null values, complex overloading/override setups, etc.)
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/27
 */
public class ClassUtilsTest
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
    void parseableAsBoolean()
    {
        Assertions.assertTrue(ClassUtils.isParseableAsBoolean("true"));
        Assertions.assertTrue(ClassUtils.isParseableAsBoolean("false"));
        Assertions.assertTrue(ClassUtils.isParseableAsBoolean("True"));
        Assertions.assertTrue(ClassUtils.isParseableAsBoolean("TruE"));
        Assertions.assertFalse(ClassUtils.isParseableAsBoolean("anything"));
        Assertions.assertFalse(ClassUtils.isParseableAsBoolean("1"));
    }
}
