package com.noleme.vault.legacy;

import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Provider;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProviderInjectionTest
{
    @Test
    public void providerInjected() throws VaultException
    {
        Vault vault = Vault.with();
        assertNotNull(vault.instance(A.class).plainProvider.get());
    }

    public static class A
    {
        private final Provider<B> plainProvider;

        @Inject
        public A(Provider<B> plainProvider) {
            this.plainProvider = plainProvider;
        }
    }

    public static class B {}
}
