package com.noleme.vault.service.annotated;

import com.noleme.vault.service.ValueProvider;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public class AnnotatedProvider implements ValueProvider<String>
{
    @Inject @Named("provider.integer") private ValueProvider<Integer> integerProvider;
    @Inject @Named("provider.string") private ValueProvider<String> stringProvider;
    @Inject @Named("provider.boolean") private ValueProvider<Boolean> booleanProvider;

    @Override
    public String provide()
    {
        return this.stringProvider.provide()+"-"+this.integerProvider.provide()+"-"+this.booleanProvider.provide();
    }
}
