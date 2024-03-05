package com.noleme.vault.builder;

import com.noleme.vault.Vault;
import com.noleme.vault.legacy.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Provider;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/13
 */
public class ProviderStage <T> implements BuildStage
{
    private final Key<T> key;
    private final Provider<T> provider;
    private final boolean closeable;

    private static final Logger logger = LoggerFactory.getLogger(ProviderStage.class);

    /**
     *
     * @param key
     * @param provider
     * @param closeable
     */
    public ProviderStage(Key<T> key, Provider<T> provider, boolean closeable)
    {
        this.key = key;
        this.provider = provider;
        this.closeable = closeable;
    }

    /**
     *
     * @param key
     * @param provider
     */
    public ProviderStage(Key<T> key, Provider<T> provider)
    {
        this(key, provider, false);
    }

    @Override
    public void build(Vault vault)
    {
        logger.debug("Populating vault using provider {} (key={}, closeable={})", this.provider.getClass().getName(), this.key, this.closeable);
        vault.register(this.key, this.provider, this.closeable);
    }
}
