package com.lumiomedical.vault.service;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public class BooleanProvider implements ValueProvider<Boolean>
{
    private final Boolean value;

    public BooleanProvider(Boolean value)
    {
        this.value = value;
    }
    
    @Override
    public Boolean provide()
    {
        return this.value;
    }
}
