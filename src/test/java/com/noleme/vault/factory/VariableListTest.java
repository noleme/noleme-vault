package com.noleme.vault.factory;

import com.noleme.vault.Vault;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.exception.VaultParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;

import java.util.List;

import static com.noleme.vault.parser.adjuster.VaultAdjuster.variables;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 01/05/2021
 */
@SuppressWarnings("resource")
public class VariableListTest
{
    private static final VaultFactory factory = new VaultFactory();

    @Test
    void listVariableTest() throws Exception
    {
        var cellar = new EnvironmentVariables("MY_VAR", "my_value")
            .execute(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/variable/list_variable.yml"));

        Assertions.assertTrue(cellar.hasVariable("my_list"));
        Assertions.assertTrue(cellar.getVariable("my_list") instanceof List);
        Assertions.assertEquals(6, cellar.getVariable("my_list", List.class).size());

        @SuppressWarnings("unchecked")
        var map = (List<Object>) cellar.getVariable("my_list", List.class);

        Assertions.assertEquals("something", map.get(0));
        Assertions.assertEquals(2345, map.get(1));
        Assertions.assertEquals(12.34, map.get(2));
        Assertions.assertEquals(false, map.get(3));
        Assertions.assertEquals("abcde", map.get(4));
        Assertions.assertEquals("my_value", map.get(5));
    }

    @Test
    void listVariableTest__invalidDeclaration()
    {
        VaultInjectionException ex = Assertions.assertThrows(VaultInjectionException.class, () -> factory.populate(new Cellar(), "com/noleme/vault/parser/variable/list_variable.invalid_declaration.yml"));
        Assertions.assertTrue(ex.getCause() instanceof VaultParserException);
    }

    @Test
    void listVariableTest__validReference()
    {
        Assertions.assertDoesNotThrow(() -> new EnvironmentVariables("MY_VAR", "my_value")
            .execute(() -> Vault.with(
                variables(vars -> vars.set("provider.list.value", vars.get("my_list"))),
                "com/noleme/vault/parser/variable/list_variable.yml",
                "com/noleme/vault/parser/provider/provider.list.yml"
            ))
        );
    }

    @Test
    void listVariableTest__invalidReference()
    {
        Assertions.assertThrows(VaultInjectionException.class, () -> new EnvironmentVariables("MY_VAR", "my_value")
            .execute(() -> Vault.with(
                variables(vars -> vars.set("provider.list.value", vars.get("my_list"))),
                "com/noleme/vault/parser/variable/list_variable.yml",
                "com/noleme/vault/parser/provider/provider.list.invalid_reference.yml"
            ))
        );
    }

    @Test
    void listVariableTest__emptyList()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> Vault.with("com/noleme/vault/parser/provider/provider.list.yml").instance(Cellar.class));
        Assertions.assertEquals(0, cellar.getVariable("provider.list.value", List.class).size());
    }
}
