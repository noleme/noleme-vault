package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.vault.container.definition.Definitions;
import com.noleme.json.Yaml;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/26
 */
public class PrintConfigurationModule implements VaultModule
{
    @Override
    public String identifier()
    {
        return "*";
    }

    @Override
    public void process(ObjectNode json, Definitions definitions)
    {
        System.out.println(Yaml.prettyPrint(json));
    }
}
