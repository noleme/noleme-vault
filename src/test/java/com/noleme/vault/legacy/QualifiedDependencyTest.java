package com.noleme.vault.legacy;

import com.noleme.vault.Provides;
import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QualifiedDependencyTest
{
    @Test
    public void qualifiedInstances() throws VaultException
    {
        Vault vault = Vault.with(new Module());
        assertEquals(FooA.class, vault.instance(Key.of(Foo.class, A.class)).getClass());
        assertEquals(FooB.class, vault.instance(Key.of(Foo.class, B.class)).getClass());
    }

    @Test
    public void injectedQualified() throws VaultException
    {
        Vault vault = Vault.with(new Module());
        Dummy dummy = vault.instance(Dummy.class);
        assertEquals(FooB.class, dummy.foo.getClass());
    }

    @Test
    public void fieldInjectedQualified() throws VaultException
    {
        Vault vault = Vault.with(new Module());
        DummyTestUnit dummy = vault.inject(new DummyTestUnit());
        assertEquals(FooA.class, dummy.foo.getClass());
    }


    interface Foo {

    }

    public static class FooA implements Foo
    {

    }

    public static class FooB implements Foo
    {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface A {

    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface B {

    }

    public static class Module {
        @Provides
        @A
        Foo a(FooA fooA) {
            return fooA;
        }

        @Provides
        @B
        Foo b(FooB fooB) {
            return fooB;
        }
    }

    public static class Dummy {
        private final Foo foo;

        @Inject
        public Dummy(@B Foo foo) {
            this.foo = foo;
        }
    }

    public static class DummyTestUnit {
        @Inject
        @A
        private Foo foo;
    }
}
