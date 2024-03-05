package com.noleme.vault.service.module;

import com.noleme.vault.Provides;
import com.noleme.vault.service.BooleanProvider;
import com.noleme.vault.service.IntegerProvider;
import com.noleme.vault.service.StringProvider;

import jakarta.inject.Named;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/13
 */
public class ProviderModule
{
    @Provides @Named("provider.boolean")
    BooleanProvider provideBoolean()
    {
        return new BooleanProvider(false);
    }

    @Provides @Named("provider.string")
    StringProvider provideString()
    {
        return new StringProvider("SomeString");
    }

    @Provides @Named("provider.integer")
    IntegerProvider provideInteger()
    {
        return new IntegerProvider(2345);
    }
}
