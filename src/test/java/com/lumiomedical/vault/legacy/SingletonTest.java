package com.lumiomedical.vault.legacy;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.exception.VaultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Provider;
import javax.inject.Singleton;

public class SingletonTest
{
    @Test
    public void nonSingleton() throws VaultException
    {
        Vault vault = Vault.with();
        Assertions.assertNotEquals(vault.instance(Plain.class), vault.instance(Plain.class));
    }

    @Test
    public void singleton() throws VaultException
    {
        Vault vault = Vault.with();
        Assertions.assertEquals(vault.instance(SingletonObj.class), vault.instance(SingletonObj.class));
    }

    @Test
    public void singletonThroughProvider() throws VaultException
    {
        Vault vault = Vault.with();
        Provider<SingletonObj> provider = vault.provider(SingletonObj.class);
        Assertions.assertEquals(provider.get(), provider.get());
    }

    public static class Plain
    {

    }

    @Singleton
    public static class SingletonObj
    {

    }
}
