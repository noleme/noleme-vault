package com.noleme.vault.legacy;

import com.noleme.vault.Provides;
import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import jakarta.inject.Named;

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
