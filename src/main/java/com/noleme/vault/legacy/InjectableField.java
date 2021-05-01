package com.noleme.vault.legacy;

import java.lang.reflect.Field;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 28/04/2021
 */
@SuppressWarnings("rawtypes")
public class InjectableField
{
    private final Field field;
    private final boolean isProvider;
    private final Key key;

    InjectableField(Field field, boolean isProvider, Key key)
    {
        this.field = field;
        this.isProvider = isProvider;
        this.key = key;
    }

    public Field getField()
    {
        return this.field;
    }

    public boolean isProvider()
    {
        return this.isProvider;
    }

    public Key getKey()
    {
        return this.key;
    }
}
