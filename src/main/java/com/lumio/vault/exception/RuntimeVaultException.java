package com.lumio.vault.exception;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/19
 */
public class RuntimeVaultException extends RuntimeException
{
    public RuntimeVaultException(String message)
    {
        super(message);
    }

    public RuntimeVaultException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
