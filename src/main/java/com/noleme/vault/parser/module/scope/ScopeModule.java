package com.noleme.vault.parser.module.scope;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.commons.container.Lists;
import com.noleme.json.Json;
import com.noleme.vault.container.definition.ServiceDefinition;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.register.ScopedDefinitions;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.vault.parser.VaultCompositeParser;
import com.noleme.vault.parser.VaultParser;
import com.noleme.vault.parser.module.*;
import com.noleme.vault.parser.resolver.FlexibleResolver;

import static com.noleme.commons.function.RethrowConsumer.rethrower;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 13/05/2021
 */
public class ScopeModule implements VaultModule
{
    @Override
    public String identifier()
    {
        return "scopes";
    }

    @Override
    public void process(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        json.fields().forEachRemaining(rethrower(entry -> {
            String identifier = entry.getKey();
            JsonNode node = entry.getValue();
            ObjectNode scopeNode = node.isTextual()
                ? Json.newObject().put("from", node.asText())
                : (ObjectNode) entry.getValue()
            ;

            scopeNode.put("identifier", identifier);

            this.extractScope(scopeNode, definitions);
        }));

        for (ScopedDefinitions scope : definitions.scopes().values())
        {
            scope.applyScope();
            for (ServiceDefinition def : scope.services().values())
                definitions.services().set(def.getIdentifier(), def);
        }
    }

    /**
     *
     * @param json
     * @param definitions
     * @throws VaultParserException
     */
    private void extractScope(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        VaultParser parser = this.provideParser(json, definitions);

        ScopedDefinitions def = (ScopedDefinitions) parser.extract(
            json.get("from").asText(),
            new ScopedDefinitions(json.get("identifier").asText())
        );

        definitions.scopes().set(json.get("identifier").asText(), def);
    }

    /**
     *
     * @param json
     * @param definitions
     * @return
     */
    private VaultParser provideParser(ObjectNode json, Definitions definitions)
    {
        return new VaultCompositeParser(
            new FlexibleResolver(),
            Lists.of(
                new ScopeVariableModule(json),
                new VariableResolvingModule(),
                new VariableReplacementModule(),
                new TagModule(),
                new ServiceModule(),
                new ScopeAliasModule(json, definitions)
            )
        );
    }
}
