package com.noleme.vault.parser;

import com.noleme.vault.Vault;
import com.noleme.vault.container.definition.ServiceProvider;
import com.noleme.vault.container.definition.ServiceValue;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultException;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.vault.service.BooleanProvider;
import com.noleme.vault.service.tag.ComponentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.noleme.vault.parser.adjuster.VaultAdjuster.*;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/28
 */
@SuppressWarnings("resource")
public class AdjusterTest
{
    @Test
    void testVariable() throws VaultParserException
    {
        var parser = new VaultCompositeParser();
        int value = 1234;

        Definitions def = parser.extract(
            "com/noleme/vault/parser/simple.json",
            new Definitions(),
            variables(vars -> vars.set("my_variable", value))
        );

        Assertions.assertEquals(value, def.variables().get("my_variable"));
    }

    @Test
    void testService() throws VaultException
    {
        var component = new ComponentService("adjusted");

        var vault = Vault.with(
            "com/noleme/vault/parser/tag/composite.yml",
            adjuster()
                .services(services -> services.set(new ServiceValue<>("my_component", component)))
                .tags(tags -> tags.register("composite_service_components", "my_component"))
                .build()
        );

        List<ComponentService> tagged = vault.instance(List.class, "composite_service_components");

        Assertions.assertNotNull(vault.instance(ComponentService.class));
        Assertions.assertEquals(1, tagged.size());
        Assertions.assertEquals(component.value(), tagged.get(0).value());
    }

    @Test
    void testVariableResolving() throws VaultParserException
    {
        var parser = new VaultCompositeParser();
        var string = "this is an entirely new string";

        Definitions defA = parser.extract("com/noleme/vault/parser/simple.json");
        Assertions.assertEquals("SomeString", ((ServiceProvider)defA.services().get("provider.string")).getMethodArgs()[0]);

        Definitions defB = parser.extract(
            "com/noleme/vault/parser/simple.json",
            new Definitions(),
            variables(vars -> vars.set("provider.string.base_value", string))
        );
        Assertions.assertEquals(string, ((ServiceProvider)defB.services().get("provider.string")).getMethodArgs()[0]);
    }

    @Test
    void testVariableReplacement() throws VaultParserException
    {
        var parser = new VaultCompositeParser();
        var string = "this is an entirely new string";

        Definitions defA = parser.extract("com/noleme/vault/parser/simple.json");
        Assertions.assertEquals("SomeString", ((ServiceProvider)defA.services().get("provider.string")).getMethodArgs()[0]);

        Definitions defB = parser.extract(
            "com/noleme/vault/parser/simple.json",
            new Definitions(),
            variables(vars -> vars.set("provider.string.value", string))
        );
        Assertions.assertEquals(string, ((ServiceProvider)defB.services().get("provider.string")).getMethodArgs()[0]);
    }

    @Test
    void testOverrideService()
    {
        BooleanProvider vanilla = Assertions.assertDoesNotThrow(() -> Vault.with(
            "com/noleme/vault/parser/service/overridable.yml"
        ).instance(BooleanProvider.class));

        Assertions.assertEquals(false, vanilla.provide());

        BooleanProvider overridden = Assertions.assertDoesNotThrow(() -> Vault.with(
            "com/noleme/vault/parser/service/overridable.yml",
            services(defs -> defs.set(new ServiceValue<>("provider.boolean", new BooleanProvider(true))))
        ).instance(BooleanProvider.class));

        Assertions.assertEquals(true, overridden.provide());
    }

    @Test
    void testCompleteService()
    {
        Assertions.assertThrows(VaultInjectionException.class, () -> Vault.with(
            "com/noleme/vault/parser/service/completeable.yml"
        ));

        BooleanProvider overridden = Assertions.assertDoesNotThrow(() -> Vault.with(
            "com/noleme/vault/parser/service/completeable.yml",
            services(defs -> defs.set(new ServiceValue<>("provider.boolean.base", new BooleanProvider(true))))
        ).instance(BooleanProvider.class));

        Assertions.assertEquals(true, overridden.provide());
    }

    public static class AdjustedService {}
}
