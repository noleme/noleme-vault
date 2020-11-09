package com.lumiomedical.vault.builder;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.exception.VaultException;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public interface BuildStage
{
    /**
     *
     * @param vault
     * @throws VaultException
     */
    void build(Vault vault) throws VaultException;
}
