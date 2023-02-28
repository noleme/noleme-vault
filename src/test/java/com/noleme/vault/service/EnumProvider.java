package com.noleme.vault.service;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public class EnumProvider implements ValueProvider<EnumProvider.TestEnum>
{
    private final TestEnum value;

    public EnumProvider(TestEnum value)
    {
        this.value = value;
    }

    @Override
    public TestEnum provide()
    {
        return this.value;
    }

    /**
     *
     * @param value
     * @return
     */
    public static EnumProvider build(TestEnum value)
    {
        return new EnumProvider(value);
    }

    public enum TestEnum {
        A, B, C
    }
}
