package com.noleme.vault.legacy;

import com.noleme.vault.Provides;
import com.noleme.vault.Vault;
import com.noleme.vault.exception.RuntimeVaultException;
import com.noleme.vault.exception.VaultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PojoProvidedThroughModuleTest
{
    @Test
    public void pojoNotProvided()
    {
        Assertions.assertThrows(RuntimeVaultException.class, () -> {
            Vault vault = Vault.with();
            vault.instance(Pojo.class);
        });
    }

    @Test
    public void pojoProvided() throws VaultException
    {
        Vault vault = Vault.with(new Module());
        assertNotNull(vault.instance(Pojo.class));
    }

    public static class Module
    {
        @Provides
        Pojo pojo() {
            return new Pojo("foo");
        }
    }

    public static class Pojo
    {
        private final String foo;

        public Pojo(String foo) {
            this.foo = foo;
        }
    }
}
