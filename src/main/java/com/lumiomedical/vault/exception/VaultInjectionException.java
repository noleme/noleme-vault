package com.lumiomedical.vault.exception;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 15/09/2014.
 */
public class VaultInjectionException extends VaultException
{
    public VaultInjectionException(String message)
    {
        super(message);
    }
    public VaultInjectionException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
