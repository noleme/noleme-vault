package com.noleme.vault.scope;

import com.noleme.vault.container.Cellar;
import com.noleme.vault.factory.VaultFactory;
import com.noleme.vault.service.IntegerProvider;
import com.noleme.vault.service.StatefulService;
import com.noleme.vault.service.scope.BaseService;
import com.noleme.vault.service.tag.CompositeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 12/05/2021
 */
@SuppressWarnings("resource")
public class ScopeTest
{
    private static final VaultFactory factory = new VaultFactory();

    @Test
    void basicScope_shouldBuild()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/scope/scopes.basic.yml"));

        var service = cellar.getService("basic_service", BaseService.class);
        Assertions.assertEquals("base_name:generic_string", service.create());

        var provider = cellar.getService("basic_provider", IntegerProvider.class);
        Assertions.assertEquals(123, provider.provide());
    }

    @Test
    void variableScope_shouldBuild()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/scope/scopes.variables.yml"));

        var service = cellar.getService("variable_service", BaseService.class);
        Assertions.assertEquals("variable_name:generic_string", service.create());

        var provider = cellar.getService("variable_provider", IntegerProvider.class);
        Assertions.assertEquals(567, provider.provide());
    }

    @Test
    void aliasScope_shouldBuild()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/scope/scopes.aliases.yml"));

        var service = cellar.getService("alias_service", BaseService.class);
        Assertions.assertEquals("base_name:alias_string", service.create());

        var provider = cellar.getService("alias_provider", IntegerProvider.class);
        Assertions.assertEquals(123, provider.provide());
    }

    @Test
    void scope_shouldNotConflict()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/scope/scopes.noconflict.yml"));

        var service = cellar.getService("noconflict_service", BaseService.class);
        Assertions.assertEquals("noconflict_name:noconflict_string", service.create());

        var provider = cellar.getService("noconflict_provider", IntegerProvider.class);
        Assertions.assertEquals(567, provider.provide());

        var localService = cellar.getService("base_service", BaseService.class);
        Assertions.assertEquals("some_name:some_string", localService.create());

        var localProvider = cellar.getService("base_provider", IntegerProvider.class);
        Assertions.assertEquals(234, localProvider.provide());
    }

    @Test
    void tags_shouldAggregate()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/scope/scopes.tagged.yml"));

        var composite = cellar.getService("composite_service", CompositeService.class);

        Assertions.assertEquals(3, composite.size());
        Assertions.assertTrue(composite.contains("a"));
        Assertions.assertTrue(composite.contains("b"));
        Assertions.assertTrue(composite.contains("local"));
    }

    @Test
    void invocations_shouldBePassedOn()
    {
        var cellar = Assertions.assertDoesNotThrow(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/scope/scopes.invocations.yml"));

        var serviceA = cellar.getService("stateful_a", StatefulService.class);
        Assertions.assertEquals(1, serviceA.getCallCount());
        Assertions.assertEquals("default_value", serviceA.getValue());

        var serviceB = cellar.getService("stateful_b", StatefulService.class);
        Assertions.assertEquals(2, serviceB.getCallCount());
        Assertions.assertEquals("custom_value", serviceB.getValue());
    }

    @Test
    void multipleScopes_shouldBuild()
    {
        var cellar = Assertions.assertDoesNotThrow(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/scope/scopes.application.yml"));

        var serviceA = cellar.getService("my_service.a", BaseService.class);
        Assertions.assertEquals("a_name:generic_string", serviceA.create());

        var serviceB = cellar.getService("my_service.b", BaseService.class);
        Assertions.assertEquals("b_name:my_string", serviceB.create());

        var serviceC = cellar.getService("my_service.c", BaseService.class);
        Assertions.assertEquals("c_name:other_string", serviceC.create());
    }
}
