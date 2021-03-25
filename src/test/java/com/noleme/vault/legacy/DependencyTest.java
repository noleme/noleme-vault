package com.noleme.vault.legacy;

import com.noleme.vault.Vault;
import com.noleme.vault.exception.RuntimeVaultException;
import com.noleme.vault.exception.VaultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DependencyTest
{
    @Test
    public void dependencyInstance() throws VaultException
    {
        Vault vault = Vault.with();
        assertNotNull(vault.instance(Plain.class));
    }

    @Test
    public void provider() throws VaultException
    {
        Vault vault = Vault.with();
        Provider<Plain> plainProvider = vault.provider(Plain.class);
        assertNotNull(plainProvider.get());
    }

    @Test
    public void unknown()
    {
        Assertions.assertThrows(RuntimeVaultException.class, () -> {
            Vault vault = Vault.with();
            vault.instance(Unknown.class);
        });
    }

    public static class Plain
    {

    }

    public static class Unknown
    {
        public Unknown(String noSuitableConstructor)
        {

        }
    }
}


