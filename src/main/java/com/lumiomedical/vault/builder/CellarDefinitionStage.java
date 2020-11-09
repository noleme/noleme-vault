package com.lumiomedical.vault.builder;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.factory.VaultFactory;

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
