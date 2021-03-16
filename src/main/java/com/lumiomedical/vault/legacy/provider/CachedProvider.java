package com.lumiomedical.vault.legacy.provider;

import javax.inject.Provider;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/16
 */
public class CachedProvider <T> implements Provider<T>
{
    private final Provider<T> provider;
    private T cache;

    /**
     *
     * @param provider
     */
    public CachedProvider(Provider<T> provider)
    {
        this.provider = provider;
        this.cache = null;
    }

    @Override
    public T get()
    {
        if (this.cache == null)
            this.createInstance();
        return this.cache;
    }

    /**
     *
     */
    synchronized void createInstance()
    {
        if (this.cache == null)
            this.cache = this.provider.get();
    }
}
