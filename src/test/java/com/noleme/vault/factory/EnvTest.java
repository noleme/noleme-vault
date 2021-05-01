package com.noleme.vault.factory;

import com.noleme.vault.container.Cellar;
import com.noleme.vault.exception.VaultException;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.service.BooleanProvider;
import com.noleme.vault.service.DoubleProvider;
import com.noleme.vault.service.IntegerProvider;
import com.noleme.vault.service.StringProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/23
 */
public class EnvTest
{
    private static final VaultFactory factory = new VaultFactory();

    @BeforeEach
    void setup()
    {
        clearEnv();
    }

    @Test
    void envVariables_shouldBeInterpreted() throws VaultException
    {
        setEnv("MY_VAR", "some interesting value");
        setEnv("MY_OTHER_VAR", "some uninteresting value");

        var cellar = factory.populate(new Cellar(), "com/noleme/vault/parser/simple_variable.yml");

        Assertions.assertEquals("MY_VAR", cellar.getVariable("my_varname"));
        Assertions.assertEquals("some interesting value", cellar.getVariable("my_ref"));
        Assertions.assertEquals("some interesting value", cellar.getVariable("my_ref2"));
        Assertions.assertEquals("some interesting value 2345", cellar.getVariable("my_multiref"));
        Assertions.assertEquals("some interesting value some interesting value 2345 some uninteresting value", cellar.getVariable("my_multiref2"));

        Assertions.assertEquals("some interesting value", ((StringProvider)cellar.getService("provider.string.1")).provide());
        Assertions.assertEquals("some uninteresting value", ((StringProvider)cellar.getService("provider.string.2")).provide());
    }

    @Test
    void envVariables_shouldBeConvertible() throws VaultException
    {
        setEnv("MY_STRING", "custom_value");
        setEnv("MY_INTEGER", "2345");
        setEnv("MY_DOUBLE", "23.45");
        setEnv("MY_BOOLEAN", "true");

        var cellar = factory.populate(new Cellar(), "com/noleme/vault/parser/env_variable.yml");

        Assertions.assertEquals("custom_value", cellar.getVariable("my_string_env"));
        Assertions.assertEquals(2345, cellar.getVariable("my_integer_env", int.class));
        Assertions.assertEquals(23.45, cellar.getVariable("my_double_env", double.class));
        Assertions.assertEquals(true, cellar.getVariable("my_boolean_env", boolean.class));
    }

    @Test
    void envVariablesWithDefaultValues_shouldBeInterpreted() throws VaultException
    {
        var noEnvCellar = factory.populate(new Cellar(), "com/noleme/vault/parser/env_variable.yml");

        Assertions.assertEquals("default_value", noEnvCellar.getVariable("my_string_defval_env"));
        Assertions.assertEquals("1234", noEnvCellar.getVariable("my_integer_defval_env"));
        Assertions.assertEquals("12.34", noEnvCellar.getVariable("my_double_defval_env"));
        Assertions.assertEquals("false", noEnvCellar.getVariable("my_boolean_defval_env"));

        setEnv("MY_STRING", "custom_value");
        setEnv("MY_INTEGER", "2345");
        setEnv("MY_DOUBLE", "23.45");
        setEnv("MY_BOOLEAN", "true");

        var cellar = factory.populate(new Cellar(), "com/noleme/vault/parser/env_variable.yml");

        Assertions.assertEquals("custom_value", cellar.getVariable("my_string_defval_env"));
        Assertions.assertEquals("2345", cellar.getVariable("my_integer_defval_env"));
        Assertions.assertEquals("23.45", cellar.getVariable("my_double_defval_env"));
        Assertions.assertEquals("true", cellar.getVariable("my_boolean_defval_env"));
    }

    @Test
    void envVariablesWithDefaultValues_shouldBeConvertible() throws VaultException
    {
        var noEnvCellar = factory.populate(new Cellar(), "com/noleme/vault/parser/env_variable.yml");

        Assertions.assertEquals("default_value", noEnvCellar.getVariable("my_string_defval_env"));
        Assertions.assertEquals(1234, noEnvCellar.getVariable("my_integer_defval_env", int.class));
        Assertions.assertEquals(12.34, noEnvCellar.getVariable("my_double_defval_env", double.class));
        Assertions.assertEquals(false, noEnvCellar.getVariable("my_boolean_defval_env", boolean.class));

        setEnv("MY_STRING", "custom_value");
        setEnv("MY_INTEGER", "2345");
        setEnv("MY_DOUBLE", "23.45");
        setEnv("MY_BOOLEAN", "true");

        var cellar = factory.populate(new Cellar(), "com/noleme/vault/parser/env_variable.yml");

        Assertions.assertEquals("custom_value", cellar.getVariable("my_string_defval_env"));
        Assertions.assertEquals(2345, cellar.getVariable("my_integer_defval_env", int.class));
        Assertions.assertEquals(23.45, cellar.getVariable("my_double_defval_env", double.class));
        Assertions.assertEquals(true, cellar.getVariable("my_boolean_defval_env", boolean.class));
    }

    @Test
    void servicesInstantiation_shouldLeverageConvertion() throws VaultInjectionException
    {
        var cellar = factory.populate(new Cellar(), "com/noleme/vault/parser/env_service.yml");

        Assertions.assertEquals("default_value", cellar.getService("my_provider.string", StringProvider.class).provide());
        Assertions.assertEquals(1234, cellar.getService("my_provider.integer", IntegerProvider.class).provide());
        Assertions.assertEquals(12.34, cellar.getService("my_provider.double", DoubleProvider.class).provide());
        Assertions.assertEquals(false, cellar.getService("my_provider.boolean", BooleanProvider.class).provide());
    }

    /**
     * Nasty hack for altering the env variable snapshot ; does not impact the actual env environment, only its in-memory representation.
     * This produces a warning for illegal reflective access, should not be an issue in this context.
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

    /**
     * Nasty hack for altering the env variable snapshot ; does not impact the actual env environment, only its in-memory representation.
     * This produces a warning for illegal reflective access, should not be an issue in this context.
     */
    @SuppressWarnings("unchecked")
    private static void clearEnv()
    {
        try {
            Map<String, String> env = System.getenv();
            Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            ((Map<String, String>) field.get(env)).clear();
        }
        catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
