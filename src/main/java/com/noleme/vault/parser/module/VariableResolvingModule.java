package com.noleme.vault.parser.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noleme.json.Json;
import com.noleme.vault.container.definition.Variable;
import com.noleme.vault.container.register.Definitions;
import com.noleme.vault.container.register.index.Variables;
import com.noleme.vault.exception.VaultParserException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/26
 */
public class VariableResolvingModule implements VaultModule
{
    private static final Pattern variablePattern = Pattern.compile("(##(.*?)##)");
    private static final Pattern envPattern = Pattern.compile("(\\$\\{([A-Za-z0-9_.-].*?)(-(.*?))?})");

    @Override
    public String identifier()
    {
        return "variables";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(ObjectNode json, Definitions definitions) throws VaultParserException
    {
        Map<String, Variable> variables = new HashMap<>();
        Queue<Variable> queue = new LinkedList<>();
        List<Variable> heap = new ArrayList<>();
        List<String> sorted = new ArrayList<>();

        for (Map.Entry<String, Object> varDef : definitions.variables().dictionary().entrySet())
        {
            Collection<String> dependencies;
            if (varDef.getValue() instanceof String)
                dependencies = findVariables((String)varDef.getValue());
            else if (varDef.getValue() instanceof List)
                dependencies = findVariablesInList((List<?>) varDef.getValue());
            else if (varDef.getValue() instanceof Map)
                dependencies = findVariablesInMap((Map<?, ?>) varDef.getValue());
            else
                dependencies = Collections.emptyList();

            Variable var = new Variable(
                varDef.getKey(),
                varDef.getValue(),
                dependencies
            );

            variables.put(varDef.getKey(), var);

            if (!var.hasDependencies())
                queue.add(var);
            else
                heap.add(var);
        }

        while (!queue.isEmpty())
        {
            Variable variable = queue.poll();
            sorted.add(variable.getName());

            Iterator<Variable> it = heap.iterator();
            V_SEARCH: while (it.hasNext())
            {
                Variable v = it.next();
                for (String dep : v.getDependencies())
                {
                    if (!sorted.contains(dep))
                        continue V_SEARCH;
                }
                queue.add(v);
                it.remove();
            }
        }

        if (!heap.isEmpty())
            throw new VaultParserException("The variable definitions contain a circular reference involving "+heap.size()+" variables.");

        for (String vk : sorted)
        {
            Variable v = variables.get(vk);
            Object replaced;

            if (v.getValue() instanceof String)
                replaced = replaceForString((String) v.getValue(), v, variables);
            else if (v.getValue() instanceof List)
                replaced = replaceForList((List<Object>) v.getValue(), v, variables);
            else if (v.getValue() instanceof Map)
                replaced = replaceForMap((Map<String, Object>) v.getValue(), v, variables);
            else
                continue;

            v.setValue(replaced);
            definitions.variables().set(v.getName(), replaced);
        }
    }

    /**
     *
     * @param string
     * @param variables
     * @return
     */
    private static Map<String, Object> findReplacements(String string, Variables variables) throws VaultParserException
    {
        Map<String, Object> replacements = new HashMap<>();

        for (String var : findVariables(string))
        {
            if (!variables.has(var))
                throw new VaultParserException("The requested variable '"+var+"' could not be found in the container.");
            replacements.put("##"+var+"##", variables.get(var));
        }

        return replacements;
    }

    /**
     *
     * @param string
     * @return
     */
    private static Collection<String> findVariables(String string)
    {
        List<String> variables = new ArrayList<>();

        Matcher matcher = variablePattern.matcher(string);
        if (matcher.find())
        {
            variables.add(matcher.group(2));

            matcher.results().forEach(matchResult -> {
                String variable = matchResult.group(2);
                variables.add(variable);
            });
        }

        return variables;
    }

    /**
     *
     * @param list
     * @return
     */
    private static Collection<String> findVariablesInList(List<?> list)
    {
        List<String> variables = new ArrayList<>();

        list.stream()
            .filter(item -> item instanceof String)
            .forEach(item -> variables.addAll(findVariables((String) item)))
        ;

        return variables;
    }

    /**
     *
     * @param map
     * @return
     */
    private static Collection<String> findVariablesInMap(Map<?, ?> map)
    {
        List<String> variables = new ArrayList<>();

        map.values().stream()
            .filter(item -> item instanceof String)
            .forEach(item -> variables.addAll(findVariables((String) item)))
        ;

        return variables;
    }

    /**
     *
     * @param val
     * @param variable
     * @param dictionary
     * @return
     */
    private static Object replaceForString(String val, Variable variable, Map<String, Variable> dictionary)
    {
        if (val == null)
            return null;

        for (String dep : variable.getDependencies())
        {
            String key = "##"+dep+"##";
            if (!val.contains(key))
                continue;

            Object value = dictionary.get(dep).getValue();

            if (key.equals(val))
            {
                val = value == null ? null : value.toString();
                break;
            }
            else
                val = val.replace(key, value == null ? "" : value.toString());
        }

        if (val != null)
            val = replaceEnv(val);

        return val;
    }

    /**
     *
     * @param val
     * @param variable
     * @param dictionary
     * @return
     */
    private static Object replaceForList(List<Object> val, Variable variable, Map<String, Variable> dictionary)
    {
        for (int i = 0 ; i < val.size() ; ++i)
        {
            Object item = val.get(i);

            if (item instanceof String)
                val.set(i, replaceForString((String) item, variable, dictionary));
        }

        return val;
    }

    /**
     *
     * @param val
     * @param variable
     * @param dictionary
     * @return
     */
    private static Object replaceForMap(Map<String, Object> val, Variable variable, Map<String, Variable> dictionary)
    {
        for (String key : val.keySet())
        {
            Object item = val.get(key);

            if (item instanceof String)
                val.put(key, replaceForString((String) item, variable, dictionary));
        }

        return val;
    }

    /**
     *
     * @param string
     * @param definitions
     * @return
     */
    public static JsonNode replace(String string, Definitions definitions) throws VaultParserException
    {
        String newString = string;
        for (Map.Entry<String, Object> replacement : findReplacements(string, definitions.variables()).entrySet())
        {
            if (replacement.getValue() == null || newString.length() == replacement.getKey().length())
                return Json.toJson(replacement.getValue());
            else
                newString = newString.replace(replacement.getKey(), replacement.getValue().toString());
        }
        newString = replaceEnv(newString);

        return Json.toJson(newString);
    }

    /**
     *
     * @param value
     * @return
     */
    private static String replaceEnv(String value)
    {
        StringBuilder builder = new StringBuilder();
        int lastStart = 0;
        int lastEnd = 0;

        Matcher matcher = envPattern.matcher(value);
        while (matcher.find())
        {
            String match = matcher.group();
            String variable = matcher.group(2);
            String defaultValue = matcher.group(4);

            String replacement = null;

            /* If we have a key to work with, we attempt to get the corresponding env variable value */
            if (variable != null)
            {
                String env = System.getenv(variable);

                if (env == null)
                    env = defaultValue;

                replacement = env;
            }

            /* If the match spans over the whole variable, we return immediately (possibly a null value) */
            if (match.length() == value.length())
                return replacement;

            /* Otherwise, we'll need to make string replacements, so we transform any null value to an empty string */
            if (replacement == null)
                replacement = "";

            if (lastStart < matcher.start())
                builder.append(value, lastEnd, matcher.start());

            builder.append(replacement);

            lastStart = matcher.start();
            lastEnd = matcher.end();
        }

        /* If we didn't match anything, we simply return the original value */
        if (builder.length() == 0)
            return value;

        if (lastEnd < value.length() - 1)
            builder.append(value.substring(lastEnd));

        return builder.toString();
    }
}
