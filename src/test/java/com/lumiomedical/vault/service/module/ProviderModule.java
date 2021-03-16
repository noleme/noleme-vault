package com.lumiomedical.vault.service.module;

import com.lumiomedical.vault.Provides;
import com.lumiomedical.vault.service.BooleanProvider;
import com.lumiomedical.vault.service.IntegerProvider;
import com.lumiomedical.vault.service.StringProvider;

import javax.inject.Named;

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
