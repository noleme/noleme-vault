package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.commons.container.Lists;
import com.noleme.json.Json;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.definition.ServiceDefinition;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.vault.parser.module.service.*;

import java.util.List;

import static com.noleme.commons.function.RethrowConsumer.rethrower;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/24
 */
public class ServiceModule implements VaultModule
{
    private final List<ServiceDefinitionExtractor> extractors;

    public ServiceModule()
    {
        this(Lists.of(
            new AliasExtractor(),
            new MethodProviderExtractor(),
            new MarkerExtractor(),
            new InstantiationExtractor(),
            new ScopedImportExtractor()
        ));
    }

    public ServiceModule(List<ServiceDefinitionExtractor> extractors)
    {
        this.extractors = extractors;
    }

    @Override
    public String identifier()
    {
        return "services";
    }

    @Override
    public void process(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        json.fields().forEachRemaining(rethrower(entry -> {
            /*
             * We start by producing an object node from which we'll attempt to extract a ServiceDefinition
             * If no "identifier" key exists, we'll use the object's key as identifier.
             * Markers (simple string entries) are transformed into an object form.
             */
            String identifier = entry.getKey();
            JsonNode node = entry.getValue();
            ObjectNode serviceNode = node.isTextual()
                ? Json.newObject().put("marker", node.asText())
                : (ObjectNode) entry.getValue()
            ;

            if (serviceNode.has("identifier") && !identifier.equals(serviceNode.get("identifier").asText()))
                throw new VaultParserException("A service was declared with conflicting identifiers, the shorthand notation '"+identifier+"' is different from the 'identifier' field of value '"+serviceNode.get("identifier").asText()+"' found in the declaration ");

            serviceNode.put("identifier", identifier);

            /* We run the extractor stack until we get one that can create a ServiceDefinition */
            for (ServiceDefinitionExtractor extractor : this.extractors)
            {
                if (extractor.accepts(serviceNode))
                {
                    ServiceDefinition def = extractor.extract(serviceNode, definitions);
                    definitions.services().set(def.getIdentifier(), def);
                    return;
                }
            }

            /* If we reach that point, no extractor was able to create a ServiceDefinition */
            throw new VaultParserException("An unknown service declaration type was found for identifier "+serviceNode.get("identifier").asText());
        }));
    }

    public ServiceModule register(ServiceDefinitionExtractor extractor)
    {
        this.extractors.add(extractor);
        return this;
    }
}
