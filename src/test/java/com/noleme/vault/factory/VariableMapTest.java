package com.noleme.vault.factory;

import com.noleme.vault.Vault;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.exception.VaultParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import java.util.Map;

import static com.noleme.vault.parser.adjuster.VaultAdjuster.variables;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 01/05/2021
 */
public class VariableMapTest
{
    private static final VaultFactory factory = new VaultFactory();

    @Test
    void mapVariableTest()
    {
        var cellar = Assertions.assertDoesNotThrow(() -> new EnvironmentVariables("MY_VAR", "my_value")
            .execute(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/variable/map_variable.yml"))
        );

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
    }

    @Test
    void mapVariableTest__invalidDeclaration()
    {
        VaultInjectionException ex = Assertions.assertThrows(VaultInjectionException.class, () -> factory.populate(
            new Cellar(),
            "com/noleme/vault/parser/variable/map_variable.invalid_declaration.yml"
        ));

        Assertions.assertTrue(ex.getCause() instanceof VaultParserException);
    }

    @Test
    void mapVariableTest__validReference()
    {
        Assertions.assertDoesNotThrow(() -> new EnvironmentVariables("MY_VAR", "my_value")
            .execute(() -> Vault.with(
                variables(vars -> vars.set("provider.map.value", vars.get("my_map"))),
                "com/noleme/vault/parser/variable/map_variable.yml",
                "com/noleme/vault/parser/provider/provider.map.yml"
            ))
        );
    }

    @Test
    void mapVariableTest__invalidReference()
    {
        Assertions.assertThrows(VaultInjectionException.class, () -> new EnvironmentVariables("MY_VAR", "my_value")
            .execute(() -> Vault.with(
                variables(vars -> vars.set("provider.map.value", vars.get("my_map"))),
                "com/noleme/vault/parser/variable/map_variable.yml",
                "com/noleme/vault/parser/provider/provider.map.invalid_reference.yml"
            ))
        );
    }

    @Test
    void mapVariableTest__emptyMap()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> Vault.with("com/noleme/vault/parser/provider/provider.map.yml").instance(Cellar.class));

        Assertions.assertEquals(0, cellar.getVariable("provider.map.value", Map.class).size());
    }
}
