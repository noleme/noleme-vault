package com.noleme.vault.service;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public class DoubleProvider implements ValueProvider<Double>
{
    private final Double value;

    public DoubleProvider(Double value)
    {
        this.value = value;
    }
    
    @Override
    public Double provide()
    {
        return this.value;
    }
}
