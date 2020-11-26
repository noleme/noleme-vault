package com.lumiomedical.vault.factory;

import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.service.StringProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/23
 */
public class EnvTest
{
    @Test
    void testEnvVariables() throws VaultException
    {
        setEnv("MY_VAR", "some interesting value");
        setEnv("MY_OTHER_VAR", "some uninteresting value");

        var factory = new VaultFactory();
        var cellar = factory.populate(new Cellar(), "com/lumiomedical/vault/parser/simple_variable.yml");

        Assertions.assertEquals("MY_VAR", cellar.getVariable("my_varname"));
        Assertions.assertEquals("some interesting value", cellar.getVariable("my_ref"));
        Assertions.assertEquals("some interesting value", cellar.getVariable("my_ref2"));
        Assertions.assertEquals("some interesting value 2345", cellar.getVariable("my_multiref"));
        Assertions.assertEquals("some interesting value some interesting value 2345 some uninteresting value", cellar.getVariable("my_multiref2"));

        Assertions.assertEquals("some interesting value", ((StringProvider)cellar.getService("provider.string.1")).provide());
        Assertions.assertEquals("some uninteresting value", ((StringProvider)cellar.getService("provider.string.2")).provide());
    }

    /**
     * Nasty hack for altering the env variable snapshot ; does not impact the actual env environment, only its in-memory representation.
     *
     * @param name
     * @param value
     */
    @SuppressWarnings("unchecked")
    private static void setEnv(String name, String value)
    {
        try {
            Map<String, String> env = System.getenv();
            Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            ((Map<String, String>) field.get(env)).put(name, value);
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
