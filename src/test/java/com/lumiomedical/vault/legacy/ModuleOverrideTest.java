package com.lumiomedical.vault.legacy;

import com.lumiomedical.vault.Provides;
import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.exception.VaultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ModuleOverrideTest
{
    @Test
    public void dependencyOverridenByModule() throws VaultException
    {
        Vault vault = Vault.with(new PlainStubOverrideModule());

        Assertions.assertEquals(PlainStub.class, vault.instance(Plain.class).getClass());
    }

    @Test
    public void moduleOverwrittenBySubClass() throws VaultException
    {
        Assertions.assertEquals("foo", Vault.with(new FooModule()).instance(String.class));
        Assertions.assertEquals("bar", Vault.with(new FooOverrideModule()).instance(String.class));
    }

    public static class Plain
    {
    }

    public static class PlainStub extends Plain
    {

    }

    public static class PlainStubOverrideModule
    {
        @Provides
        public Plain plain(PlainStub plainStub) {
            return plainStub;
        }

    }

    public static class FooModule
    {
        @Provides
        String foo() {
            return "foo";
        }
    }

    public static class FooOverrideModule extends FooModule
    {
        @Provides
        @Override
        String foo() {
            return "bar";
        }
    }




}
