package com.noleme.vault.parser.module.scope;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.vault.parser.module.VaultModule;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 15/05/2021
 */
class ScopeVariableModule implements VaultModule
{
    private final ObjectNode scopeDefinition;

    public ScopeVariableModule(ObjectNode scopeDefinition)
    {
        this.scopeDefinition = scopeDefinition;
    }

    @Override
    public String identifier()
    {
        return "variables";
    }

    @Override
    public void process(ObjectNode node, Definitions definitions) throws VaultParserException
    {
        if (!this.scopeDefinition.has("variables"))
            return;

        this.scopeDefinition.get("variables").fields().forEachRemaining(entry -> {
            definitions.variables().set(entry.getKey(), entry.getValue());
        });
    }
}
