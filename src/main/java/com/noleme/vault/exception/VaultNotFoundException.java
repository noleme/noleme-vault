package com.noleme.vault.exception;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 25/01/15.
 */
public class VaultNotFoundException extends RuntimeVaultException
{
    public VaultNotFoundException(String message) { super(message); }
    public VaultNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
