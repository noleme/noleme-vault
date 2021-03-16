package com.lumiomedical.vault.service.module;

import com.lumiomedical.vault.Provides;
import com.lumiomedical.vault.service.BooleanProvider;
import com.lumiomedical.vault.service.IntegerProvider;
import com.lumiomedical.vault.service.StringProvider;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/03/13
 */
public class SingletonProviderModule
{
    @Singleton @Provides @Named("provider.boolean")
    BooleanProvider provideBoolean()
    {
        return new BooleanProvider(false);
    }

    @Singleton @Provides @Named("provider.string")
    StringProvider provideString()
    {
        return new StringProvider("SomeString");
    }

    @Singleton @Provides @Named("provider.integer")
    IntegerProvider provideInteger()
    {
        return new IntegerProvider(2345);
    }
}
