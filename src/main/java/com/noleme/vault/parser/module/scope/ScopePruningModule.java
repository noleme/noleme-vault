package com.noleme.vault.parser.module.scope;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.register.ScopedDefinitions;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.vault.parser.module.VaultModule;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 27/12/2021
 */
public class ScopePruningModule implements VaultModule
{
    @Override
    public String identifier()
    {
        return "scopes";
    }

    @Override
    public void process(ObjectNode node, Definitions definitions) throws VaultParserException
    {
        /* We loop over all scoped definitions and check if they are still actively referenced (ie. the ScopedImportExtractor did find services using them) */
        for (ScopedDefinitions scope : definitions.scopes().values())
        {
            if (scope.isActivelyReferenced())
                continue;

            for (String inactiveScopedService : scope.services().keys())
                definitions.services().remove(inactiveScopedService);
        }
    }
}
