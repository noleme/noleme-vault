package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.json.Json;
import com.noleme.json.JsonException;
import com.noleme.vault.container.definition.ServiceProvider;
import com.noleme.vault.container.definition.ServiceValue;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultParserException;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 05/05/2021
 */
public class GenericModule<T> implements VaultModule
{
    private final String identifier;
    private final Class<T> type;
    private final Processor<T> processor;

    private static final ObjectMapper mapper = Json.newDefaultMapper()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
    ;

    /**
     * Creates a ${GenericModule} which will attempt to map a given node to an instance of the provided type.
     * The processor instance will be able to manipulate the resulting instance and perform changes over the ${Definitions} instance.
     *
     * @param identifier the identifier for the module, ie the root-level key in the json object
     * @param type the target type for deserialization
     * @param processor the processor responsible for handling the resulting deserialized instance
     */
    public GenericModule(String identifier, Class<T> type, Processor<T> processor)
    {
        this.identifier = identifier;
        this.type = type;
        this.processor = processor;
    }

    /**
     * Creates a ${GenericModule} which will attempt to map a given node to an instance of the provided type.
     * This constructor will use a default ${Processor} which will simply register the resulting instance as a service.
     *
     * @param identifier the identifier for the module, ie the root-level key in the json object
     * @param type the target type for deserialization
     * @param name the name of the service associated to the resulting deserialized instance
     */
    public GenericModule(String identifier, Class<T> type, String name)
    {
        this(identifier, type, (cfg, defs) -> defs.services().set(name, new ServiceValue<>(name, cfg)));
    }

    @Override
    public String identifier()
    {
        return this.identifier;
    }

    @Override
    public void process(ObjectNode node, Definitions definitions) throws VaultParserException
    {
        try {
            T config = Json.fromJson(mapper, node, this.type);
            this.processor.process(config, definitions);
        }
        catch (JsonException e) {
            throw new VaultParserException("The provided node could not be deserialized into an object of type "+this.type.getName(), e);
        }
    }

    public interface Processor<T>
    {
        void process(T config, Definitions definitions) throws VaultParserException;
    }
}
