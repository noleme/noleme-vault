package com.noleme.vault.builder;

import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;

/**
 * A BuildStage is responsible for the completion of a provided Vault instance using various means.
 * Examples of BuildStage implementations include the ModuleStage (register services from @Provides methods) or the CellarStage (register services from a Cellar instance).
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public interface BuildStage
{
    /**
     *
     * @param vault the Vault instance to register services to
     * @throws VaultException if an error occurred during the build process
     */
    void build(Vault vault) throws VaultException;
}
