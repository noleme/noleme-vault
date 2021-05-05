package com.noleme.vault.service;

import java.util.Map;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 01/05/2021
 */
public class MapProvider <T> implements ValueProvider<Map<String, T>>
{
    private final Map<String, T> values;

    public MapProvider(Map<String, T> values)
    {
        this.values = values;
    }

    @Override
    public Map<String, T> provide()
    {
        return this.values;
    }
}
