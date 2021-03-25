package com.noleme.vault.builder;

import com.noleme.vault.Vault;
import com.noleme.vault.exception.VaultException;
import com.noleme.vault.legacy.VaultLegacyCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * This BuildStage implementation registers services using @Provides annotations found in the provided "module" instances.
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public class ModuleStage implements BuildStage
{
    private final Object[] modules;

    private static final Logger logger = LoggerFactory.getLogger(ModuleStage.class);

    public ModuleStage(Object... modules)
    {
        this.modules = modules;
    }

    @Override
    public void build(Vault vault) throws VaultException
    {
        for (Object module : this.modules)
        {
            logger.debug("Populating vault using module class {}", module.getClass().getName());

            if (module instanceof Class)
                throw new VaultException(String.format("%s provided as class instead of an instance.", ((Class) module).getName()));
            for (Method providerMethod : VaultLegacyCompiler.providers(module.getClass()))
                VaultLegacyCompiler.providerMethod(vault, module, providerMethod);
        }
    }
}
