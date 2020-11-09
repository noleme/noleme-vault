package com.lumiomedical.vault.legacy;

import com.lumiomedical.vault.Provides;
import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.exception.VaultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;

public class PolymorphicDependencyTest
{
    @Test
    public void multipleImplementations() throws VaultException
    {
        Vault vault = Vault.with(new Module());
        Assertions.assertEquals(FooA.class, vault.instance(Key.of(Foo.class, "A")).getClass());
        Assertions.assertEquals(FooB.class, vault.instance(Key.of(Foo.class, "B")).getClass());
    }

    public static class Module
    {
        @Provides
        @Named("A")
        Foo a(FooA fooA) {
            return fooA;
        }

        @Provides @Named("B")
        Foo a(FooB fooB) {
            return fooB;
        }
    }

    interface Foo
    {

    }

    public static class FooA implements Foo
    {
        @Inject
        public FooA() { }
    }

    public static class FooB implements Foo
    {
        @Inject
        public FooB() { }
    }
}
