package com.noleme.vault.tag;

import com.noleme.json.Json;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.container.Invocation;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.definition.ServiceInstantiation;
import com.noleme.vault.container.definition.ServiceTag;
import com.noleme.vault.container.definition.Tag;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.factory.VaultFactory;
import com.noleme.vault.parser.VaultCompositeParser;
import com.noleme.vault.parser.module.GenericModule;
import com.noleme.vault.service.StringProvider;
import com.noleme.vault.service.tag.ComponentService;
import com.noleme.vault.service.tag.CompositeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 09/05/2021
 */
public class CompositeTest
{
    private static final VaultFactory factory = new VaultFactory();

    @Test
    void compositeTest__builds()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> {
            Definitions definitions = factory.parser().extractOrigin(List.of(
                "com/noleme/vault/parser/tag/composite.yml",
                "com/noleme/vault/parser/tag/component.yml"
            ), new Definitions());

            return factory.populate(new Cellar(), definitions);
        });

        var composite = cellar.getService("composite_service", CompositeService.class);

        Assertions.assertTrue(composite.contains("string_a"));
        Assertions.assertTrue(composite.contains("string_b"));
        Assertions.assertTrue(composite.contains("string_c"));
        Assertions.assertEquals(4, composite.size());

        var compositeAlt = cellar.getService("composite_service.alt", CompositeService.class);

        Assertions.assertFalse(compositeAlt.contains("string_a"));
        Assertions.assertTrue(compositeAlt.contains("string_b"));
        Assertions.assertTrue(compositeAlt.contains("string_c"));
        Assertions.assertEquals(2, compositeAlt.size());
    }

    @Test
    void compositeTest__does_not_build()
    {
        Definitions definitions = Assertions.assertDoesNotThrow(() -> factory.parser().extractOrigin(List.of(
            "com/noleme/vault/parser/tag/composite_without_declaration.yml",
            "com/noleme/vault/parser/tag/component.yml"
        ), new Definitions()));

        Assertions.assertThrows(VaultInjectionException.class, () -> factory.populate(new Cellar(), definitions));
    }

    @Test
    void emptyCompositeTest__builds()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/tag/composite.yml"));

        var composite = cellar.getService("composite_service", CompositeService.class);
        Assertions.assertEquals(0, composite.size());
    }

    @Test
    void compositeTest__conflictsWithService()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> factory.populate(new Cellar(), "com/noleme/vault/parser/tag/conflict_with_service.yml"));

        var notComposite = cellar.getService("composite_service_components");
        Assertions.assertTrue(notComposite instanceof StringProvider);
    }

    @Test
    void customConfig__builds()
    {
        Cellar cellar = Assertions.assertDoesNotThrow(() -> {
            var parser = new VaultCompositeParser().register(new CustomModule());
            var factory = new VaultFactory(parser);

            var definitions = factory.parser().extractOrigin(List.of(
                "com/noleme/vault/parser/tag/custom_module.yml",
                "com/noleme/vault/parser/tag/component.yml"
            ), new Definitions());

            return factory.populate(new Cellar(), definitions);
        });

        var composite = cellar.getService("my_custom_composite", CustomComposite.class);
        Assertions.assertEquals(3, composite.size());
        Assertions.assertEquals(7, composite.weight());
    }

    public static class CustomModule extends GenericModule<CustomConfig>
    {
        public CustomModule()
        {
            super("custom", CustomConfig.class, CustomModule::process);
        }

        private static void process(CustomConfig cfg, Definitions defs)
        {
            if (!cfg.enabled)
                return;

            var definition = new ServiceInstantiation(cfg.name, CustomComposite.class.getName());

            for (String tagName : cfg.tagNames)
            {
                if (!defs.services().has(tagName))
                    defs.services().set(tagName, new ServiceTag(tagName));

                for (Tag tag : defs.tags().forIdentifier(tagName))
                {
                    CustomTag ctag = Json.fromJson(tag.getNode(), CustomTag.class);

                    definition.addInvocation(new Invocation("addComponent", defs.services().reference(tag.getService())));
                    definition.addInvocation(new Invocation("addWeight", ctag.weight));
                }
            }

            defs.services().set(cfg.name, definition);
        }
    }

    public static class CustomComposite
    {
        private final List<ComponentService> components = new ArrayList<>();
        private int weight = 0;

        public void addComponent(ComponentService component) { this.components.add(component); }
        public void addWeight(int weight) { this.weight += weight; }
        
        public int size() { return this.components.size(); }
        public int weight() { return this.weight; }
    }

    public static class CustomConfig
    {
        public boolean enabled;
        public String name;
        public List<String> tagNames;
    }

    public static class CustomTag
    {
        public int weight = 0;
    }
}
