package com.lumio.vault.service;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public class IntegerProvider implements ValueProvider<Integer>
{
    private final Integer value;

    public IntegerProvider(Integer value)
    {
        this.value = value;
    }
    
    @Override
    public Integer provide()
    {
        return this.value;
    }
}
