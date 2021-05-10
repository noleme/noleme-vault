package com.noleme.vault.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.definition.Definitions;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.vault.exception.VaultStructureException;
import com.noleme.vault.parser.adjuster.VaultAdjuster;
import com.noleme.vault.parser.module.*;
import com.noleme.vault.parser.preprocessor.VaultPreprocessor;
import com.noleme.vault.parser.resolver.FlexibleResolver;
import com.noleme.vault.parser.resolver.VaultResolver;
import com.noleme.vault.parser.resolver.source.Source;
import com.noleme.commons.container.Lists;
import com.noleme.json.Json;
import com.noleme.json.JsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.noleme.commons.function.RethrowFunction.rethrower;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 05/02/15.
 */
@SuppressWarnings("rawtypes")
public class VaultCompositeParser implements VaultParser
{
    private final VaultResolver resolver;
    private static final String rootIdentifier = "*";
    private static final Set<String> coreDirectives = Set.of("imports");
    private final List<VaultPreprocessor> preprocessors = new ArrayList<>();
    /* Module stacks */
    private final List<VaultModule> preModules = Lists.of(
        new VariableRegistrationModule()
    );
    private final List<VaultModule> modules = Lists.of(
        new VariableResolvingModule(),
        new VariableReplacementModule(),
        new TagModule(),
        new ServiceModule()
    );

    private static final Logger logger = LoggerFactory.getLogger(VaultCompositeParser.class);

    public VaultCompositeParser()
    {
        this(new FlexibleResolver());
    }

    /**
     *
     * @param resolver
     */
    public VaultCompositeParser(VaultResolver resolver)
    {
        this.resolver = resolver;
    }

    @Override
    public Definitions extract(Collection<Source> sources, Definitions definitions, Collection<VaultAdjuster> adjusters) throws VaultParserException
    {
        ObjectNode json = Json.newObject();
        for (Source source : sources)
            json = this.compileNode(source, json);

        this.launchModules(this.preModules, json, definitions);
        for (VaultAdjuster adjuster : adjusters)
            adjuster.adjust(definitions.variables());
        this.launchModules(this.modules, json, definitions);

        return definitions;
    }

    @Override
    public Definitions extractOrigin(Collection<String> origins, Definitions definitions, Collection<VaultAdjuster> adjusters) throws VaultParserException
    {
        try {
            return this.extract(
                origins.stream().map(rethrower(this.resolver::resolve)).collect(Collectors.toList()),
                definitions,
                adjusters
            );
        }
        catch (JsonException e) {
            throw new VaultParserException("The configuration file could not be loaded, the input appears to contain invalid JSON.", e);
        }
    }

    /**
     *
     * @param modules
     * @param json
     * @param definitions
     * @throws VaultParserException
     */
    private void launchModules(List<VaultModule> modules, ObjectNode json, Definitions definitions) throws VaultParserException
    {
        for (VaultModule module : modules)
        {
            if (rootIdentifier.equals(module.identifier()))
                module.process(json, definitions);
            else if (json.has(module.identifier()))
                module.process((ObjectNode) json.get(module.identifier()), definitions);
        }
    }

    /**
     *
     * @param source
     * @param rootNode
     * @return
     * @throws VaultParserException
     */
    private ObjectNode compileNode(Source source, ObjectNode rootNode) throws VaultParserException
    {
        try {
            ObjectNode json = source.interpret();

            logger.debug("Running parser preprocessors ({} preprocessors registered)", this.preprocessors.size());

            /* The preprocessor pass can be used to perform compatibility adjustments or last-minute changes over uncontrolled inputs */
            for (VaultPreprocessor preprocessor : this.preprocessors)
                json = preprocessor.preprocess(json);

            this.validateStructure(json);

            for (String imp : this.getImports(json))
            {
                Source importSource = this.resolver.resolve(imp);
                rootNode = this.compileNode(importSource, rootNode);
            }

            /* For each module found in the source's JSON, we make sure it is represented in the root node */
            var moduleIterator = json.fields();
            while (moduleIterator.hasNext())
            {
                var moduleEntry = moduleIterator.next();
                String module = moduleEntry.getKey();

                if (coreDirectives.contains(module))
                    continue;

                if (!moduleEntry.getValue().isObject())
                    throw new VaultParserException("Modules are expected to be objects, provided value for "+module+" as specified in "+source.getOrigin()+" was "+moduleEntry.getValue().getNodeType().name());

                ObjectNode moduleItems = (ObjectNode) moduleEntry.getValue();

                /* If it doesn't exist yet, we simply copy the items into the root node */
                if (!rootNode.has(module))
                    rootNode.set(module, moduleItems);
                /* If it exists, we iterate over the items and replace them in the root node */
                else {
                    ObjectNode rootModuleItems = (ObjectNode) rootNode.get(module);

                    var itemIterator = moduleItems.fields();
                    while (itemIterator.hasNext())
                    {
                        var itemEntry = itemIterator.next();
                        rootModuleItems.set(itemEntry.getKey(), itemEntry.getValue());
                    }
                }
            }

            return rootNode;
        }
        catch (VaultStructureException e) {
            throw new VaultParserException("A structural error has been detected in input "+source.getOrigin()+".", e);
        }
    }

    @Override
    public VaultParser registerPreprocessor(VaultPreprocessor preprocessor)
    {
        this.preprocessors.add(preprocessor);
        return this;
    }

    @Override
    public VaultParser register(VaultModule module)
    {
        this.modules.add(module);
        return this;
    }

    /**
     *
     * @param json
     */
    private void validateStructure(ObjectNode json) throws VaultStructureException
    {
        Iterator<String> keys = json.fieldNames();

        Set<String> validKeys = new HashSet<>(coreDirectives);
        validKeys.addAll(getModuleIdentifiers(preModules));
        validKeys.addAll(getModuleIdentifiers(modules));

        while (keys.hasNext())
        {
            String key = keys.next();
            if (!validKeys.contains(key))
                throw new VaultStructureException("An unknown module '"+key+"' has been requested, available modules are: "+ String.join(", ", validKeys));
        }
    }

    /**
     *
     * @param modules
     * @return
     */
    private static Set<String> getModuleIdentifiers(Collection<VaultModule> modules)
    {
        return modules.stream()
            .map(VaultModule::identifier)
            .filter(identifier -> !identifier.equals(rootIdentifier))
            .collect(Collectors.toSet())
        ;
    }

    /**
     *
     * @param json
     * @return
     */
    private List<String> getImports(ObjectNode json)
    {
        List<String> found = new ArrayList<>();
        if (json.has("imports"))
        {
            for (JsonNode node : json.get("imports"))
                found.add(node.asText());
        }
        return found;
    }
}
