package com.lumiomedical.vault.container.definition.hook;

import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.VaultCompilationException;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/25
 */
public interface PostResolvingHandler
{
    /**
     *
     * @param definitions
     * @throws VaultCompilationException
     */
    void handle(Definitions definitions) throws VaultCompilationException;
}
