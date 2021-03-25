package com.noleme.vault.builder;

import com.noleme.vault.Vault;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.legacy.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This BuildStage implementation registers services using a pre-existing Cellar instance.
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public class CellarStage implements BuildStage
{
    private final Cellar cellar;

    private static final Logger logger = LoggerFactory.getLogger(CellarStage.class);

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
        logger.debug("Populating vault using pre-compiled Cellar ({} service and {} variable references found)", this.cellar.getServices().size(), this.cellar.getVariables().size());

        this.cellar.getServices().forEach((name, service) -> {
            Key namedKey = Key.of(service.getClass(), name);
            Key typeKey = Key.of(service.getClass());
            boolean isCloseable = this.cellar.isCloseable(name);

            vault.register(typeKey, () -> service, isCloseable);
            vault.register(namedKey, () -> service, isCloseable);
        });
        this.cellar.getVariables().forEach((name, variable) -> {
            Key namedKey = Key.of(getVariableClass(variable), name);
            Key typeKey = Key.of(getVariableClass(variable));

            vault.register(namedKey, () -> variable);
            vault.register(typeKey, () -> variable);
        });
    }

    /**
     *
     * @param instance
     * @return
     */
    private static Class<?> getVariableClass(Object instance)
    {
        return instance != null ? instance.getClass() : Object.class;
    }
}
