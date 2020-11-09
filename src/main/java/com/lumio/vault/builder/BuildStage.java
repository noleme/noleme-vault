package com.lumio.vault.builder;

import com.lumio.vault.Vault;
import com.lumio.vault.exception.VaultException;

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
