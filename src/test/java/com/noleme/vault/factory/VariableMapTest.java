package com.noleme.vault.factory;

import com.noleme.vault.Vault;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.exception.VaultParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.noleme.vault.factory.EnvTest.clearEnv;
import static com.noleme.vault.factory.EnvTest.setEnv;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 01/05/2021
 */
public class VariableMapTest
{
    private static final VaultFactory factory = new VaultFactory();

    @BeforeEach
    void setup()
    {
        clearEnv();
    }

    @Test
    void mapVariableTest()
    {
        Assertions.assertDoesNotThrow(() -> {
            setEnv("MY_VAR", "my_value");

            var cellar = factory.populate(new Cellar(), "com/noleme/vault/parser/variable/map_variable.yml");

            Assertions.assertTrue(cellar.hasVariable("my_map"));
            Assertions.assertTrue(cellar.getVariable("my_map") instanceof Map);
            Assertions.assertEquals(6, cellar.getVariable("my_map", Map.class).size());

            @SuppressWarnings("unchecked")
            var map = (Map<String, Object>) cellar.getVariable("my_map", Map.class);

            Assertions.assertEquals("something", map.get("my_string"));
            Assertions.assertEquals(2345, map.get("my_integer"));
            Assertions.assertEquals(12.34, map.get("my_double"));
            Assertions.assertEquals(false, map.get("my_boolean"));
            Assertions.assertEquals("abcde", map.get("my_ref"));
            Assertions.assertEquals("my_value", map.get("my_env"));
        });
    }

    @Test
    void mapVariableTest__invalidDeclaration()
    {
        Assertions.assertThrows(VaultInjectionException.class, () -> {
            try {
                factory.populate(new Cellar(), "com/noleme/vault/parser/variable/map_variable.invalid_declaration.yml");
            }
            catch (VaultInjectionException e) {
                Assertions.assertTrue(e.getCause() instanceof VaultParserException);
                throw e;
            }
        });
    }

    @Test
    void mapVariableTest__validReference()
    {
        setEnv("MY_VAR", "my_value");

        Assertions.assertDoesNotThrow(() -> Vault.with(
            vars -> vars.set("provider.map.value", vars.get("my_map")),
            "com/noleme/vault/parser/variable/map_variable.yml",
            "com/noleme/vault/parser/provider/provider.map.yml"
        ));
    }

    @Test
    void mapVariableTest__invalidReference()
    {
        setEnv("MY_VAR", "my_value");

        Assertions.assertThrows(VaultInjectionException.class, () -> Vault.with(
            vars -> vars.set("provider.map.value", vars.get("my_map")),
            "com/noleme/vault/parser/variable/map_variable.yml",
            "com/noleme/vault/parser/provider/provider.map.invalid_reference.yml"
        ));
    }

    @Test
    void mapVariableTest__emptyMap()
    {
        Assertions.assertDoesNotThrow(() -> {
            var cellar = Vault.with("com/noleme/vault/parser/provider/provider.map.yml").instance(Cellar.class);

            Assertions.assertEquals(0, cellar.getVariable("provider.map.value", Map.class).size());
        });
    }
}
