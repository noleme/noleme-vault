package com.noleme.vault.legacy;

import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FieldInjectionTest
{
    @Test
    public void fieldsInjected() throws VaultException
    {
        Vault vault = Vault.with();
        Target target = vault.inject(new Target());
        assertNotNull(target.a);
    }


    public static class Target
    {
        @Inject
        private A a;
    }

    public static class A
    {

    }
}
