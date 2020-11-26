package com.lumiomedical.vault.builder;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.factory.VaultFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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
    private final Consumer<Definitions> adjuster;

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
    public CellarPathStage(VaultFactory factory, String path, Consumer<Definitions> adjuster)
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
    public CellarPathStage(VaultFactory factory, List<String> paths, Consumer<Definitions> adjuster)
    {
        this.factory = factory;
        this.paths = paths;
        this.adjuster = adjuster;
    }

    @Override
    public void build(Vault vault) throws VaultException
    {
        Definitions definitions = new Definitions();

        for (String path : this.paths)
            definitions = this.factory.parse(path, definitions);

        this.adjuster.accept(definitions);

        Cellar cellar = this.factory.populate(new Cellar(), definitions);

        new CellarStage(cellar).build(vault);
    }
}
