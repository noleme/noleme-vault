package com.lumio.vault.builder;

import com.lumio.vault.Vault;
import com.lumio.vault.exception.VaultException;
import com.lumio.vault.legacy.VaultLegacyCompiler;

import java.lang.reflect.Method;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public class ModuleStage implements BuildStage
{
    private final Object[] modules;

    public ModuleStage(Object... modules)
    {
        this.modules = modules;
    }

    @Override
    public void build(Vault vault) throws VaultException
    {
        for (Object module : this.modules)
        {
            if (module instanceof Class)
                throw new VaultException(String.format("%s provided as class instead of an instance.", ((Class) module).getName()));
            for (Method providerMethod : VaultLegacyCompiler.providers(module.getClass()))
                VaultLegacyCompiler.providerMethod(vault, module, providerMethod);
        }
    }
}
