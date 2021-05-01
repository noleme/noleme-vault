package com.noleme.vault.parser;

import com.noleme.vault.container.definition.Definitions;
import com.noleme.vault.exception.VaultParserException;
import com.noleme.vault.parser.adjuster.VaultAdjuster;
import com.noleme.vault.parser.module.VaultModule;
import com.noleme.vault.parser.preprocessor.VaultPreprocessor;
import com.noleme.vault.parser.resolver.source.Source;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 05/02/15.
 */
@SuppressWarnings("rawtypes")
public interface VaultParser
{
    default Definitions extract(Source source) throws VaultParserException
    {
        return this.extract(source, new Definitions(), Collections.emptyList());
    }

    default Definitions extract(Source source, Definitions definitions, VaultAdjuster... adjusters) throws VaultParserException
    {
        return this.extract(source, definitions, Arrays.asList(adjusters));
    }

    default Definitions extract(Source source, Definitions definitions, Collection<VaultAdjuster> adjusters) throws VaultParserException
    {
        return this.extract(Collections.singletonList(source), definitions, adjusters);
    }

    default Definitions extract(Collection<Source> sources, Definitions definitions, VaultAdjuster... adjusters) throws VaultParserException
    {
        return this.extract(sources, definitions, Arrays.asList(adjusters));
    }

    /**
     *
     * @param sources
     * @param definitions
     * @param adjusters
     * @return
     * @throws VaultParserException
     */
    Definitions extract(Collection<Source> sources, Definitions definitions, Collection<VaultAdjuster> adjusters) throws VaultParserException;

    default Definitions extract(String origin) throws VaultParserException
    {
        return this.extract(origin, new Definitions());
    }

    default Definitions extract(String origin, Definitions definitions) throws VaultParserException
    {
        return this.extract(origin, definitions, Collections.emptyList());
    }

    default Definitions extract(String origin, Definitions definitions, VaultAdjuster... adjusters) throws VaultParserException
    {
        return this.extract(origin, definitions, Arrays.asList(adjusters));
    }

    default Definitions extract(String origin, Definitions definitions, Collection<VaultAdjuster> adjusters) throws VaultParserException
    {
        return this.extractOrigin(Collections.singletonList(origin), definitions, adjusters);
    }

    default Definitions extractOrigin(Collection<String> origins, Definitions definitions, VaultAdjuster... adjusters) throws VaultParserException
    {
        return this.extractOrigin(origins, definitions, Arrays.asList(adjusters));
    }

    /**
     *
     * @param origins
     * @param definitions
     * @param adjusters
     * @return
     * @throws VaultParserException
     */
    Definitions extractOrigin(Collection<String> origins, Definitions definitions, Collection<VaultAdjuster> adjusters) throws VaultParserException;

    /**
     * Register a custom preprocessor for performing modifications over configuration nodes before compilation passes.
     *
     * @param preprocessor a vault preprocessor instance
     * @return
     */
    VaultParser registerPreprocessor(VaultPreprocessor preprocessor);

    /**
     * Register a custom module for performing additional processing over configuration nodes.
     *
     * @param module a vault module instance
     * @return
     */
    VaultParser register(VaultModule module);
}
