package com.lumio.vault.parser.resolver.source;

import com.lumio.commons.file.Charsets;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/07/06
 */
public class RawSource extends Source
{
    /**
     *
     * @param data
     * @param dialect
     */
    public RawSource(String data, Dialect dialect)
    {
        this(new ByteArrayInputStream(data.getBytes(Charsets.UTF_8.getCharset())), dialect);
    }

    /**
     *
     * @param data
     * @param dialect
     */
    public RawSource(InputStream data, Dialect dialect)
    {
        super(null, data, dialect);
    }

    @Override
    public String getOrigin()
    {
        return "raw source";
    }
}
