package com.noleme.vault.parser;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.definition.ServiceProvider;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.factory.VaultFactory;
import com.noleme.vault.parser.module.GenericModule;
import com.noleme.vault.parser.module.VaultModule;
import com.noleme.vault.service.StringProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/24
 */
public class ModuleTest
{
    @Test
    void test() throws VaultInjectionException
    {
        VaultParser parser = new VaultCompositeParser().register(new TestModule());
        var cellar = new VaultFactory(parser).populate(new Cellar(), "com/noleme/vault/parser/module/module.yml");

        makeAssertions(cellar);
    }

    @Test
    void genericTest__shouldFail()
    {
        VaultParser parser = new VaultCompositeParser().register(new GenericModule<>("custom", NonMatchingTestConfig.class, (config, defs) -> {}));
        Assertions.assertThrows(VaultInjectionException.class, () -> {
            new VaultFactory(parser).populate(new Cellar(), "com/noleme/vault/parser/module/module.yml");
        });
    }

    @Test
    void genericTest() throws VaultInjectionException
    {
        VaultParser parser = new VaultCompositeParser().register(new GenericModule<>("custom", TestConfig.class, (config, defs) -> {
            config.providers.forEach(id -> {
                var def = new ServiceProvider(id, StringProvider.class.getName(), "build");
                def.setMethodArgs(new Object[]{ config.value });

                defs.services().set(id, def);
            });
        }));
        var cellar = new VaultFactory(parser).populate(new Cellar(), "com/noleme/vault/parser/module/module.yml");

        makeAssertions(cellar);
    }

    @Test
    void genericClassTest() throws VaultInjectionException
    {
        VaultParser parser = new VaultCompositeParser().register(new TypedTestModule());
        var cellar = new VaultFactory(parser).populate(new Cellar(), "com/noleme/vault/parser/module/module.yml");

        makeAssertions(cellar);
    }

    public static class TestModule implements VaultModule
    {
        @Override
        public String identifier()
        {
            return "custom";
        }

        @Override
        public void process(ObjectNode json, Definitions definitions)
        {
            String value = json.get("value").asText();

            json.get("providers").elements().forEachRemaining(entry -> {
                var id = entry.asText();
                var def = new ServiceProvider(id, StringProvider.class.getName(), "build");
                def.setMethodArgs(new Object[]{ value });

                definitions.services().set(id, def);
            });
        }
    }

    private static void makeAssertions(Cellar cellar)
    {
        Assertions.assertEquals("this_is_my_new_string", cellar.getService("my_provider.a", StringProvider.class).provide());
        Assertions.assertEquals("this_is_my_new_string", cellar.getService("my_provider.b", StringProvider.class).provide());
        Assertions.assertEquals("this_is_my_new_string", cellar.getService("my_provider.c", StringProvider.class).provide());
    }

    public static class TestConfig
    {
        public String value;
        public List<String> providers = new ArrayList<>();
    }

    public static class NonMatchingTestConfig
    {
        public Long value;
        public List<String> providers = new ArrayList<>();
    }

    public static class TypedTestModule extends GenericModule<TestConfig>
    {
        public TypedTestModule()
        {
            super("custom", TestConfig.class, TypedTestModule::process);
        }

        private static void process(TestConfig config, Definitions definitions)
        {
            config.providers.forEach(id -> {
                var def = new ServiceProvider(id, StringProvider.class.getName(), "build");
                def.setMethodArgs(new Object[]{ config.value });

                definitions.services().set(id, def);
            });
        }
    }
}
