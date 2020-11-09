package com.lumiomedical.vault.exception;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 21/10/2018
 */
public class VaultResolverException extends VaultParserException
{
    public VaultResolverException(String message) { super(message); }
    public VaultResolverException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
