package com.noleme.vault.exception;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public class VaultCompilationException extends VaultException
{
    public VaultCompilationException(String message)
    {
        super(message);
    }
    public VaultCompilationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
