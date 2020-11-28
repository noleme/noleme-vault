package com.lumiomedical.vault.builder;

import com.lumiomedical.vault.Vault;
import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.factory.VaultFactory;
import com.lumiomedical.vault.parser.adjuster.VaultAdjuster;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder class for queuing BuildStages over the creation of a Vault instance.
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public final class VaultBuilder
{
    private final List<BuildStage> stages;
    private VaultFactory factory;

    /**
     *
     */
    public VaultBuilder()
    {
        this.stages = new ArrayList<>();
        this.factory = new VaultFactory();

        this.with(new SelfStage());
    }

    /**
     *
     * @param factory
     * @return
     */
    public VaultBuilder setFactory(VaultFactory factory)
    {
        this.factory = factory;
        return this;
    }

    /**
     *
     * @return
     */
    public VaultFactory getFactory()
    {
        return this.factory;
    }

    /**
     *
     * @param stage
     * @return
     */
    public VaultBuilder with(BuildStage stage)
    {
        this.stages.add(stage);
        return this;
    }

    /**
     *
     * @param cellar
     * @return
     */
    public VaultBuilder with(Cellar cellar)
    {
        this.stages.add(new CellarStage(cellar));
        return this;
    }

    /**
     *
     * @param definitions
     * @return
     */
    public VaultBuilder with(Definitions definitions)
    {
        this.stages.add(new CellarDefinitionStage(this.factory, definitions));
        return this;
    }

    /**
     *
     * @param path
     * @return
     */
    public VaultBuilder with(String path)
    {
        this.stages.add(new CellarPathStage(this.factory, path));
        return this;
    }

    /**
     *
     * @param path
     * @param adjuster
     * @return
     */
    public VaultBuilder with(String path, VaultAdjuster adjuster)
    {
        this.stages.add(new CellarPathStage(this.factory, path, adjuster));
        return this;
    }

    /**
     *
     * @param paths
     * @return
     */
    public VaultBuilder with(List<String> paths)
    {
        this.stages.add(new CellarPathStage(this.factory, paths));
        return this;
    }

    /**
     *
     * @param paths
     * @param adjuster
     * @return
     */
    public VaultBuilder with(List<String> paths, VaultAdjuster adjuster)
    {
        this.stages.add(new CellarPathStage(this.factory, paths, adjuster));
        return this;
    }

    /**
     *
     * @param adjuster
     * @param paths
     * @return
     */
    public VaultBuilder with(VaultAdjuster adjuster, String... paths)
    {
        this.stages.add(new CellarPathStage(this.factory, List.of(paths), adjuster));
        return this;
    }

    /**
     *
     * @param modules
     * @return
     */
    public VaultBuilder with(Object... modules)
    {
        this.stages.add(new ModuleStage(modules));
        return this;
    }

    /**
     *
     * @return
     * @throws VaultException
     */
    public Vault build() throws VaultException
    {
        Vault vault = new Vault();

        for (BuildStage stage : this.stages)
            stage.build(vault);

        return vault;
    }
}
