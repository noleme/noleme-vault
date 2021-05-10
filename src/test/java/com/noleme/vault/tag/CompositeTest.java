package com.noleme.vault.tag;

import com.noleme.vault.container.Cellar;
import com.noleme.vault.container.definition.Definitions;
import com.noleme.vault.exception.VaultInjectionException;
import com.noleme.vault.factory.VaultFactory;
import com.noleme.vault.service.tag.CompositeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 09/05/2021
 */
public class CompositeTest
{
    private static final VaultFactory factory = new VaultFactory();

    @Test
    void compositeTest()
    {
        Assertions.assertDoesNotThrow(() -> {
            var definitions = factory.parser().extractOrigin(List.of(
                "com/noleme/vault/parser/tag/composite.yml",
                "com/noleme/vault/parser/tag/component.yml"
            ), new Definitions());

            var cellar = factory.populate(new Cellar(), definitions);

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
        });
    }

    @Test
    void compositeWithFailsafeTest()
    {
        Assertions.assertDoesNotThrow(() -> {
            var definitions = factory.parser().extractOrigin(List.of(
                "com/noleme/vault/parser/tag/composite_with_failsafe.yml",
                "com/noleme/vault/parser/tag/component.yml"
            ), new Definitions());

            var cellar = factory.populate(new Cellar(), definitions);

            var composite = cellar.getService("composite_service", CompositeService.class);
            Assertions.assertTrue(composite.contains("string_a"));
            Assertions.assertTrue(composite.contains("string_b"));
            Assertions.assertTrue(composite.contains("string_c"));
            Assertions.assertEquals(4, composite.size());
        });
    }

    @Test
    void emptyCompositeTest()
    {
        Assertions.assertThrows(VaultInjectionException.class, () -> {
            var definitions = factory.parser().extractOrigin(List.of(
                "com/noleme/vault/parser/tag/composite.yml"
            ), new Definitions());

            var cellar = factory.populate(new Cellar(), definitions);

            var composite = cellar.getService("composite_service", CompositeService.class);
            Assertions.assertEquals(0, composite.size());
        });
    }

    @Test
    void emptyCompositeWithFailsafeTest()
    {
        Assertions.assertDoesNotThrow(() -> {
            var definitions = factory.parser().extractOrigin(List.of(
                "com/noleme/vault/parser/tag/composite_with_failsafe.yml"
            ), new Definitions());

            var cellar = factory.populate(new Cellar(), definitions);

            var composite = cellar.getService("composite_service", CompositeService.class);
            Assertions.assertEquals(0, composite.size());
        });
    }

    @Test
    void tagConflictWithServiceTest()
    {
        Assertions.assertThrows(VaultInjectionException.class, () -> {
            factory.populate(new Cellar(), "com/noleme/vault/parser/tag/conflict_with_service.yml");
        });
    }
}
