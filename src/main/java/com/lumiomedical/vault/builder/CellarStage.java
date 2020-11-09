package com.lumiomedical.vault.builder;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.legacy.Key;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public class CellarStage implements BuildStage
{
    private final Cellar cellar;

    /**
     *
     * @param cellar
     */
    public CellarStage(Cellar cellar)
    {
        this.cellar = cellar;
    }

    @Override
    public void build(Vault vault)
    {
        this.cellar.getServices().forEach((name, service) -> {
            vault.register(Key.of(service.getClass(), name), () -> service, this.cellar.isCloseable(name));
        });
        this.cellar.getVariables().forEach((name, variable) -> {
            Key key = variable != null
                ? Key.of(variable.getClass(), name)
                : Key.of(Object.class, name)
            ;
            vault.register(key, () -> variable);
        });
    }
}
