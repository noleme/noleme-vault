package com.lumio.vault.exception;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 05/02/15.
 */
public class VaultParserException extends VaultException
{
    public VaultParserException(String message) { super(message); }
    public VaultParserException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
