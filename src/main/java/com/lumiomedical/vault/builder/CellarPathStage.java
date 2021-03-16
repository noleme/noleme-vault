package com.lumiomedical.vault.builder;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.factory.VaultFactory;
import com.lumiomedical.vault.parser.adjuster.VaultAdjuster;
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
        this(factory, path, defs -> {});
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
        this(factory, paths, defs -> {});
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
        Definitions definitions = new Definitions();

        definitions = this.factory.parser().extractOrigin(this.paths, definitions, this.adjuster);

        logger.debug("Populating vault using configuration files {}", this.paths);

        Cellar cellar = this.factory.populate(new Cellar(), definitions);

        new CellarStage(cellar).build(vault);
    }
}
