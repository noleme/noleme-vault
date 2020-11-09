package com.lumiomedical.vault.service;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/06/01
 */
public class ServiceProvider
{
    public static <T> ValueProvider<T> provide(ValueProvider<T> valueProvider)
    {
        return valueProvider;
    }
}
