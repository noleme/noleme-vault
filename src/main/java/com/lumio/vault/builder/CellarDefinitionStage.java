package com.lumio.vault.builder;

import com.lumio.vault.Vault;
import com.lumio.vault.container.Cellar;
import com.lumio.vault.container.definition.Definitions;
import com.lumio.vault.exception.VaultException;
import com.lumio.vault.factory.VaultFactory;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public class CellarDefinitionStage implements BuildStage
{
    private final VaultFactory factory;
    private final Definitions definitions;

    /**
     *
     * @param factory
     * @param definitions
     */
    public CellarDefinitionStage(VaultFactory factory, Definitions definitions)
    {
        this.factory = factory;
        this.definitions = definitions;
    }

    @Override
    public void build(Vault vault) throws VaultException
    {
        Cellar cellar = this.factory.populate(new Cellar(), this.definitions);
        new CellarStage(cellar).build(vault);
    }
}
