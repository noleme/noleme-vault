package com.noleme.vault.parser;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.json.Json;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.noleme.commons.function.RethrowConsumer.rethrower;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/24
 */
public class PreprocessorTest
{
    @Test
    void test() throws VaultParserException
    {
        var parser = new VaultCompositeParser();

        Assertions.assertThrows(VaultParserException.class, () -> {
            parser.extract("com/noleme/vault/parser/simple.old_style.json");
        });

        parser.registerPreprocessor(PreprocessorTest::preprocessor);

        Assertions.assertDoesNotThrow(() -> parser.extract("com/noleme/vault/parser/simple.old_style.json"));

        Definitions definitions = parser.extract("com/noleme/vault/parser/simple.old_style.json");

        ParserTest.makeAssertions(definitions);
    }

    /**
     *
     * @param node
     * @return
     * @throws VaultParserException
     */
    private static ObjectNode preprocessor(ObjectNode node) throws VaultParserException
    {
        if (!node.has("services") || !node.get("services").isArray())
            return node;

        var objServices = Json.newObject();

        node.get("services").forEach(rethrower(serviceNode -> {

            if (!serviceNode.has("identifier"))
                throw new VaultParserException("A service node declared in an old-style service array has no identifier field.");

            objServices.set(
                serviceNode.get("identifier").asText(),
                serviceNode
            );
        }));

        node.set("services", objServices);

        return node;
    }
}
