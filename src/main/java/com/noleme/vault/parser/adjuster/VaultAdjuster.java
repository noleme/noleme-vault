package com.noleme.vault.parser.adjuster;

import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.register.index.Scopes;
import com.noleme.vault.container.register.index.Services;
import com.noleme.vault.container.register.index.Tags;
import com.noleme.vault.container.register.index.Variables;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.vault.parser.module.VaultModule;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/28
 */
public final class VaultAdjuster
{
    private final ScopeAdjuster scope;
    private final ServiceAdjuster service;
    private final TagAdjuster tag;
    private final VariableAdjuster variable;

    private static final VaultAdjuster NO_OP = VaultAdjuster.adjuster().build();

    VaultAdjuster(
        ScopeAdjuster scope,
        ServiceAdjuster service,
        TagAdjuster tag,
        VariableAdjuster variable
    ) {
        this.scope = scope;
        this.service = service;
        this.tag = tag;
        this.variable = variable;
    }

    public static VaultAdjuster noop()
    {
        return NO_OP;
    }

    public static VaultAdjusterBuilder adjuster()
    {
        return new VaultAdjusterBuilder();
    }

    public static VaultAdjuster scopes(ScopeAdjuster adjuster)
    {
        return new VaultAdjusterBuilder().scopes(adjuster).build();
    }

    public static VaultAdjuster services(ServiceAdjuster adjuster)
    {
        return new VaultAdjusterBuilder().services(adjuster).build();
    }

    public static VaultAdjuster tags(TagAdjuster adjuster)
    {
        return new VaultAdjusterBuilder().tags(adjuster).build();
    }

    public static VaultAdjuster variables(VariableAdjuster adjuster)
    {
        return new VaultAdjusterBuilder().variables(adjuster).build();
    }

    public void adjust(Scopes scopes) throws VaultParserException
    {
        this.scope.adjust(scopes);
    }

    public void adjust(Services services) throws VaultParserException
    {
        this.service.adjust(services);
    }

    public void adjust(Tags tags) throws VaultParserException
    {
        this.tag.adjust(tags);
    }

    public void adjust(Variables variables) throws VaultParserException
    {
        this.variable.adjust(variables);
    }

    public static class VaultAdjusterBuilder
    {
        private ScopeAdjuster scope = scopes -> {};
        private ServiceAdjuster service = services -> {};
        private TagAdjuster tag = tags -> {};
        private VariableAdjuster variable = variables -> {};

        private VaultAdjusterBuilder() {}

        public VaultAdjuster build()
        {
            return new VaultAdjuster(this.scope, this.service, this.tag, this.variable);
        }

        public VaultAdjusterBuilder scopes(ScopeAdjuster adjuster)
        {
            this.scope = adjuster;
            return this;
        }

        public VaultAdjusterBuilder services(ServiceAdjuster adjuster)
        {
            this.service = adjuster;
            return this;
        }

        public VaultAdjusterBuilder tags(TagAdjuster adjuster)
        {
            this.tag = adjuster;
            return this;
        }

        public VaultAdjusterBuilder variables(VariableAdjuster adjuster)
        {
            this.variable = adjuster;
            return this;
        }
    }

    public static class VaultAdjusterMapper
    {
        private final Map<Class<? extends VaultModule>, VaultAdjusterAccessor<?>> adjusters;

        public VaultAdjusterMapper()
        {
            this.adjusters = new HashMap<>();
        }

        public boolean knows(VaultModule module)
        {
            return this.adjusters.containsKey(module.getClass());
        }

        public VaultAdjusterAccessor<?> get(VaultModule module)
        {
            return this.adjusters.get(module.getClass());
        }

        public VaultAdjusterMapper register(Class<? extends VaultModule> moduleType, VaultAdjusterAccessor<?> accessorFunction)
        {
            this.adjusters.put(moduleType, accessorFunction);
            return this;
        }
    }

    public static class VaultAdjusterAccessor<T>
    {
        private final Function<Definitions, T> definitionsAccessor;
        private final Function<VaultAdjuster, Adjuster<T>> adjusterAccessor;

        public VaultAdjusterAccessor(Function<Definitions, T> definitionsAccessor, Function<VaultAdjuster, Adjuster<T>> adjusterAccessor)
        {
            this.definitionsAccessor = definitionsAccessor;
            this.adjusterAccessor = adjusterAccessor;
        }

        public void adjust(VaultAdjuster adjuster, Definitions defs) throws VaultParserException
        {
            this.adjusterAccessor.apply(adjuster).adjust(this.definitionsAccessor.apply(defs));
        }
    }
}
