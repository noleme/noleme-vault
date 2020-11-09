package com.lumiomedical.vault.exception;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 15/09/2014.
 */
public class VaultInstantiationException extends VaultException
{
    public VaultInstantiationException(String message)
    {
        super(message);
    }
    public VaultInstantiationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
