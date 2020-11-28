package com.lumiomedical.vault.parser.resolver.source;

import com.lumiomedical.vault.parser.resolver.dialect.interpreter.DialectInterpreter;
import com.noleme.commons.file.Charsets;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/07/06
 */
public class RawSource extends Source<InputStream>
{
    /**
     *
     * @param data
     * @param interpreter
     */
    public RawSource(String data, DialectInterpreter<InputStream> interpreter)
    {
        this(new ByteArrayInputStream(data.getBytes(Charsets.UTF_8.getCharset())), interpreter);
    }

    /**
     *
     * @param data
     * @param interpreter
     */
    public RawSource(InputStream data, DialectInterpreter<InputStream> interpreter)
    {
        super(null, data, interpreter);
    }

    @Override
    public String getOrigin()
    {
        return "raw source";
    }
}
