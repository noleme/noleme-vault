package com.noleme.vault.builder;

import com.noleme.vault.Vault;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultException;
import com.noleme.vault.factory.VaultFactory;
import com.noleme.vault.parser.adjuster.VaultAdjuster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * This BuildStage implementation registers services using a path to a Cellar configuration file.
 * The provided VaultFactory will be used to build the Cellar once the path is resolved.
 * An optional "adjuster" can perform modifications before populating the Cellar.
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public class CellarPathStage implements BuildStage
{
    private final VaultFactory factory;
    private final List<String> paths;
    private final VaultAdjuster adjuster;

    private static final Logger logger = LoggerFactory.getLogger(CellarPathStage.class);

    /**
     *
     * @param factory
     * @param path
     */
    public CellarPathStage(VaultFactory factory, String path)
    {
        this(factory, path, VaultAdjuster.noop());
    }

    /**
     *
     * @param factory
     * @param path
     */
    public CellarPathStage(VaultFactory factory, String path, VaultAdjuster adjuster)
    {
        this(factory, Collections.singletonList(path), adjuster);
    }

    /**
     *
     * @param factory
     * @param paths
     */
    public CellarPathStage(VaultFactory factory, List<String> paths)
    {
        this(factory, paths, VaultAdjuster.noop());
    }

    /**
     *
     * @param factory
     * @param paths
     * @param adjuster
     */
    public CellarPathStage(VaultFactory factory, List<String> paths, VaultAdjuster adjuster)
    {
        this.factory = factory;
        this.paths = paths;
        this.adjuster = adjuster;
    }

    @Override
    public void build(Vault vault) throws VaultException
    {
        Definitions definitions = this.factory.parser().extractOrigin(this.paths, new Definitions(), this.adjuster);

        logger.debug("Populating vault using configuration files {}", this.paths);

        Cellar cellar = this.factory.populate(new Cellar(), definitions);

        new CellarStage(cellar).build(vault);
    }
}
