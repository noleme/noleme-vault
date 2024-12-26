package com.noleme.vault.factory;

import com.noleme.vault.container.Cellar;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.service.BooleanProvider;
import com.noleme.vault.service.DoubleProvider;
import com.noleme.vault.service.IntegerProvider;
import com.noleme.vault.service.StringProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/23
 */
public class EnvTest
{
    private static final VaultFactory factory = new VaultFactory();

    @Test
    void envVariables_shouldBeInterpreted() throws Exception
    {
        var cellar = new EnvironmentVariables("MY_VAR", "some interesting value")
            .and("MY_OTHER_VAR", "some uninteresting value")
            .execute(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/simple_variable.yml"));

        Assertions.assertEquals("MY_VAR", cellar.getVariable("my_varname"));
        Assertions.assertEquals("some interesting value", cellar.getVariable("my_ref"));
        Assertions.assertEquals("some interesting value", cellar.getVariable("my_ref2"));
        Assertions.assertEquals("some interesting value 2345", cellar.getVariable("my_multiref"));
        Assertions.assertEquals("some interesting value some interesting value 2345 some uninteresting value", cellar.getVariable("my_multiref2"));

        Assertions.assertEquals("some interesting value", ((StringProvider)cellar.getService("provider.string.1")).provide());
        Assertions.assertEquals("some uninteresting value", ((StringProvider)cellar.getService("provider.string.2")).provide());
    }

    @Test
    void envVariables_absentShouldBeNull() throws Exception
    {
        var cellar = new EnvironmentVariables("MY_VAR", "some interesting value")
            .execute(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/simple_variable.yml"));

        Assertions.assertEquals("some interesting value", ((StringProvider)cellar.getService("provider.string.1")).provide());
        Assertions.assertNull(((StringProvider)cellar.getService("provider.string.2")).provide());
    }

    @Test
    void envVariables_shouldBeConvertible() throws Exception
    {
        var cellar = new EnvironmentVariables("MY_STRING", "custom_value")
            .and("MY_INTEGER", "2345")
            .and("MY_DOUBLE", "23.45")
            .and("MY_BOOLEAN", "true")
            .execute(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/variable/env_variable.yml"));

        Assertions.assertEquals("custom_value", cellar.getVariable("my_string_env"));
        Assertions.assertEquals(2345, cellar.getVariable("my_integer_env", int.class));
        Assertions.assertEquals(23.45, cellar.getVariable("my_double_env", double.class));
        Assertions.assertEquals(true, cellar.getVariable("my_boolean_env", boolean.class));
    }

    @Test
    void envVariablesWithDefaultValues_shouldBeInterpreted() throws Exception
    {
        var noEnvCellar = factory.populate(new Cellar(), "com/noleme/vault/parser/variable/env_variable.yml");

        Assertions.assertEquals("default_value", noEnvCellar.getVariable("my_string_defval_env"));
        Assertions.assertEquals("1234", noEnvCellar.getVariable("my_integer_defval_env"));
        Assertions.assertEquals("12.34", noEnvCellar.getVariable("my_double_defval_env"));
        Assertions.assertEquals("false", noEnvCellar.getVariable("my_boolean_defval_env"));
        Assertions.assertEquals("default_value", noEnvCellar.getVariable("my_string_defval_alt_env"));
        Assertions.assertEquals("1234", noEnvCellar.getVariable("my_integer_defval_alt_env"));
        Assertions.assertEquals("12.34", noEnvCellar.getVariable("my_double_defval_alt_env"));
        Assertions.assertEquals("false", noEnvCellar.getVariable("my_boolean_defval_alt_env"));

        var cellar = new EnvironmentVariables("MY_STRING", "custom_value")
            .and("MY_INTEGER", "2345")
            .and("MY_DOUBLE", "23.45")
            .and("MY_BOOLEAN", "true")
            .execute(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/variable/env_variable.yml"));

        Assertions.assertEquals("custom_value", cellar.getVariable("my_string_defval_env"));
        Assertions.assertEquals("2345", cellar.getVariable("my_integer_defval_env"));
        Assertions.assertEquals("23.45", cellar.getVariable("my_double_defval_env"));
        Assertions.assertEquals("true", cellar.getVariable("my_boolean_defval_env"));
        Assertions.assertEquals("custom_value", cellar.getVariable("my_string_defval_alt_env"));
        Assertions.assertEquals("2345", cellar.getVariable("my_integer_defval_alt_env"));
        Assertions.assertEquals("23.45", cellar.getVariable("my_double_defval_alt_env"));
        Assertions.assertEquals("true", cellar.getVariable("my_boolean_defval_alt_env"));
    }

    @Test
    void envVariablesWithDefaultValues_shouldBeConvertible() throws Exception
    {
        var noEnvCellar = factory.populate(new Cellar(), "com/noleme/vault/parser/variable/env_variable.yml");

        Assertions.assertEquals("default_value", noEnvCellar.getVariable("my_string_defval_env"));
        Assertions.assertEquals(1234, noEnvCellar.getVariable("my_integer_defval_env", int.class));
        Assertions.assertEquals(12.34, noEnvCellar.getVariable("my_double_defval_env", double.class));
        Assertions.assertEquals(false, noEnvCellar.getVariable("my_boolean_defval_env", boolean.class));
        Assertions.assertEquals("default_value", noEnvCellar.getVariable("my_string_defval_alt_env"));
        Assertions.assertEquals(1234, noEnvCellar.getVariable("my_integer_defval_alt_env", int.class));
        Assertions.assertEquals(12.34, noEnvCellar.getVariable("my_double_defval_alt_env", double.class));
        Assertions.assertEquals(false, noEnvCellar.getVariable("my_boolean_defval_alt_env", boolean.class));

        var cellar = new EnvironmentVariables("MY_STRING", "custom_value")
            .and("MY_INTEGER", "2345")
            .and("MY_DOUBLE", "23.45")
            .and("MY_BOOLEAN", "true")
            .execute(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/variable/env_variable.yml"));

        Assertions.assertEquals("custom_value", cellar.getVariable("my_string_defval_env"));
        Assertions.assertEquals(2345, cellar.getVariable("my_integer_defval_env", int.class));
        Assertions.assertEquals(23.45, cellar.getVariable("my_double_defval_env", double.class));
        Assertions.assertEquals(true, cellar.getVariable("my_boolean_defval_env", boolean.class));
        Assertions.assertEquals("custom_value", cellar.getVariable("my_string_defval_alt_env"));
        Assertions.assertEquals(2345, cellar.getVariable("my_integer_defval_alt_env", int.class));
        Assertions.assertEquals(23.45, cellar.getVariable("my_double_defval_alt_env", double.class));
        Assertions.assertEquals(true, cellar.getVariable("my_boolean_defval_alt_env", boolean.class));
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
}
