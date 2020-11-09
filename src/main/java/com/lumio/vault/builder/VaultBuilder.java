package com.lumio.vault.builder;

import com.lumio.vault.Vault;
import com.lumio.vault.container.Cellar;
import com.lumio.vault.container.definition.Definitions;
import com.lumio.vault.exception.VaultException;
import com.lumio.vault.factory.VaultFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
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
    public VaultBuilder with(String path, Consumer<Definitions> adjuster)
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
    public VaultBuilder with(List<String> paths, Consumer<Definitions> adjuster)
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
    public VaultBuilder with(Consumer<Definitions> adjuster, String... paths)
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
