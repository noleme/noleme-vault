package com.noleme.vault.builder;

import com.noleme.vault.Vault;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.container.definition.Definitions;
import com.noleme.vault.exception.VaultException;
import com.noleme.vault.factory.VaultFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This BuildStage implementation registers services using a pre-existing Definitions instance.
 * The Definitions will be used to populate a new Cellar, which in turn will be used for registering services into the Vault.
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public class CellarDefinitionStage implements BuildStage
{
    private final VaultFactory factory;
    private final Definitions definitions;

    private static final Logger logger = LoggerFactory.getLogger(CellarDefinitionStage.class);

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
        logger.debug("Populating vault using pre-compiled Definitions");

        Cellar cellar = this.factory.populate(new Cellar(), this.definitions);
        new CellarStage(cellar).build(vault);
    }
}
