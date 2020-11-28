package com.lumiomedical.vault.parser.module;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.container.definition.ServiceProvider;
import com.lumiomedical.vault.service.StringProvider;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/24
 */
public class CustomModule implements VaultModule
{
    @Override
    public String identifier()
    {
        return "custom";
    }

    @Override
    public void process(ObjectNode json, Definitions definitions)
    {
        String value = json.get("value").asText();

        json.get("providers").elements().forEachRemaining(entry -> {
            var id = entry.asText();
            var def = new ServiceProvider(id, StringProvider.class.getName(), "build");
            def.setMethodArgs(new Object[]{ value });

            definitions.setDefinition(id, def);
        });
    }
}
