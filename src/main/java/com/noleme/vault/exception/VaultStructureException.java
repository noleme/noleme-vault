package com.noleme.vault.exception;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 05/02/15.
 */
public class VaultStructureException extends VaultException
{
    public VaultStructureException(String message) { super(message); }
    public VaultStructureException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
