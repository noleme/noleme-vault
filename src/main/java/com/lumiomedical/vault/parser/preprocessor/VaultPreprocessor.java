package com.lumiomedical.vault.parser.preprocessor;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lumiomedical.vault.exception.VaultParserException;

/**
 * The preprocessors are expected to be used to perform compatibility adjustments or last-minute changes over uncontrolled inputs.
 * Preprocessing should happen before structure validation and before compilation.
 *
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2021/01/25
 */
public interface VaultPreprocessor
{
    /**
     * Can alter a given configuration node, or leave it as is, before it goes through compilation.
     *
     * @param node A configuration node
     * @return an altered configuration node
     * @throws VaultParserException
     */
    ObjectNode preprocess(ObjectNode node) throws VaultParserException;
}
