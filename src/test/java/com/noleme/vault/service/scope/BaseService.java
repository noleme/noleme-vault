package com.noleme.vault.service.scope;

import com.noleme.vault.service.ValueProvider;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 12/05/2021
 */
public class BaseService
{
    private final String name;
    private final ValueProvider<String> provider;

    public BaseService(String name, ValueProvider<String> provider)
    {
        this.name = name;
        this.provider = provider;
    }

    public String create()
    {
        return this.name+":"+this.provider.provide();
    }
}
