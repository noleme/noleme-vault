package com.lumiomedical.vault.service;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public class StringProvider implements ValueProvider<String>
{
    private final String value;

    public StringProvider(String value)
    {
        this.value = value;
    }

    @Override
    public String provide()
    {
        return this.value;
    }

    /**
     *
     * @param value
     * @return
     */
    public static StringProvider build(String value)
    {
        return new StringProvider(value);
    }
}
