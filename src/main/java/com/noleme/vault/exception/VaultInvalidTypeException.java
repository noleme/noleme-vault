package com.noleme.vault.exception;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 25/01/15.
 */
public class VaultInvalidTypeException extends RuntimeVaultException
{
    public VaultInvalidTypeException(String message) { super(message); }
    public VaultInvalidTypeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
