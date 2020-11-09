package com.lumio.vault.legacy;

import com.lumio.vault.Provides;
import com.lumio.vault.Vault;
import com.lumio.vault.exception.VaultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Named;

public class NamedDependencyTest
{
    @Test
    public void namedInstanceWithModule() throws VaultException
    {
        Vault vault = Vault.with(new HelloWorldModule());
        Assertions.assertEquals("Hello!", vault.instance(Key.of(String.class, "hello")));
        Assertions.assertEquals("Hi!", vault.instance(Key.of(String.class, "hi")));
    }

    public static class HelloWorldModule
    {
        @Provides
        @Named("hello")
        String hello() {
            return "Hello!";
        }

        @Provides
        @Named("hi")
        String hi() {
            return "Hi!";
        }
    }

}
