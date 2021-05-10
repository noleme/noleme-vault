package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.definition.Definitions;
import com.noleme.vault.container.definition.ServiceTag;
import com.noleme.vault.exception.VaultParserException;

import static com.noleme.commons.function.RethrowConsumer.rethrower;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 10/05/2021
 */
public class TagModule implements VaultModule
{
    @Override
    public String identifier()
    {
        return "tags";
    }

    @Override
    public void process(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        json.fields().forEachRemaining(rethrower(entry -> {
            String identifier = entry.getKey();
            ObjectNode tagNode = (ObjectNode) entry.getValue();

            if (tagNode.has("identifier") && !identifier.equals(tagNode.get("identifier").asText()))
                throw new VaultParserException("A tag was declared with conflicting identifiers, the shorthand notation '"+identifier+"' is different from the 'identifier' field of value '"+tagNode.get("identifier").asText()+"' found in the declaration ");

            tagNode.put("identifier", identifier);

            this.extractTag(tagNode, definitions);
        }));
    }

    /**
     *
     * @param definition
     * @param definitions
     */
    private void extractTag(ObjectNode definition, Definitions definitions)
    {
        String identifier = definition.get("identifier").asText();

        definitions.services().set(identifier, new ServiceTag(identifier));
    }
}
