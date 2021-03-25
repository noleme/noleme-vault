package com.noleme.vault.legacy;

import com.noleme.vault.Provides;
import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AmbiguousModuleTest
{
    @Test
    public void ambiguousModule() throws VaultException
    {
        var vault = Vault.with(new Module());

        Assertions.assertEquals("zoo", vault.instance(String.class));
    }

    public static class Module {
        @Provides String bar() { return "bar"; }
        @Provides String foo() { return "foo"; }
        @Provides String zoo() { return "zoo"; }
    }
}
