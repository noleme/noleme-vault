package com.lumio.vault.legacy;

import com.lumio.vault.Provides;
import com.lumio.vault.Vault;
import com.lumio.vault.exception.VaultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AmbiguousModuleTest
{
    @Test
    public void ambiguousModule()
    {
        Assertions.assertThrows(VaultException.class, () -> {
            Vault.with(new Module());
        });
    }

    public static class Module {
        @Provides
        String foo() {
            return "foo";
        }

        @Provides
        String bar() {
            return "bar";
        }
    }
}
