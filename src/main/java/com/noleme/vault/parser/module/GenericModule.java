package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.json.Json;
import com.noleme.json.JsonException;
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
     *
     * @param identifier
     * @param type
     */
    public GenericModule(String identifier, Class<T> type, Processor<T> processor)
    {
        this.identifier = identifier;
        this.type = type;
        this.processor = processor;
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
