package com.noleme.vault.parser.module.scope;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.definition.ServiceAlias;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.register.index.Reference;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.vault.parser.module.VaultModule;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 15/05/2021
 */
class ScopeAliasModule implements VaultModule
{
    private final ObjectNode scopeJson;
    private final Definitions scopeDefinitions;

    public ScopeAliasModule(ObjectNode scopeDefinition, Definitions scopeDefinitions)
    {
        this.scopeJson = scopeDefinition;
        this.scopeDefinitions = scopeDefinitions;
    }

    @Override
    public String identifier()
    {
        return "services";
    }

    @Override
    public void process(ObjectNode node, Definitions definitions) throws VaultParserException
    {
        if (!this.scopeJson.has("aliases"))
            return;

        this.scopeJson.get("aliases").fields().forEachRemaining(entry -> {
            Reference ref = this.scopeDefinitions.services().reference(entry.getValue().asText());
            var alias = new ServiceAlias(entry.getKey(), ref);

            definitions.services().set(entry.getKey(), alias);
        });
    }
}
