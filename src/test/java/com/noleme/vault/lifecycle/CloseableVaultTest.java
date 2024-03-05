package com.noleme.vault.lifecycle;

import com.noleme.vault.Provides;
import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;

public class CloseableVaultTest
{
    @Test
    public void autocloseableService() throws VaultException
    {
        var vault = Vault.with(new CloseableVaultTestModule());

        var service = vault.instance(CloseableService.class);

        Assertions.assertTrue(service.state());

        vault.close();

        Assertions.assertFalse(service.state());
    }

    private static class CloseableVaultTestModule
    {
        @Provides @Singleton
        public static CloseableService provides()
        {
            return new CloseableService();
        }
    }

    private static class CloseableService implements AutoCloseable
    {
        final AtomicBoolean state;

        private CloseableService()
        {
            this.state = new AtomicBoolean(true);
        }

        public boolean state()
        {
            return this.state.get();
        }

        @Override
        public void close()
        {
            this.state.set(false);
        }
    }
}
