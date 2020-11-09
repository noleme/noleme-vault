package com.lumio.vault.container.definition;

import com.lumio.vault.exception.VaultCompilationException;

import java.util.*;

/**
 * @author Pierre Lecerf (pierre@noleme.com)
 * Created on 26/06/2016
 */
public class Variable
{
    private final String name;
    private Object value;
    private Collection<String> dependencies;

    /**
     *
     * @param name
     * @param value
     */
    public Variable(String name, Object value)
    {
        this.name = name;
        this.value = value;
        this.dependencies = value instanceof String ? findVariables((String)value) : new ArrayList<>();
    }

    /**
     *
     * @return
     */
    public String getName()
    {
        return this.name;
    }

    /**
     *
     * @return
     */
    public Object getValue()
    {
        return this.value;
    }

    /**
     *
     * @param value
     */
    public void setValue(Object value)
    {
        this.value = value;
    }

    /**
     *
     * @return
     */
    public boolean hasDependencies()
    {
        return !this.dependencies.isEmpty();
    }

    /**
     *
     * @return
     */
    public Collection<String> getDependencies()
    {
        return this.dependencies;
    }

    /**
     *
     * @param string
     * @param definitions
     * @return
     */
    public static Map<String, Object> findReplacements(String string, Definitions definitions) throws VaultCompilationException
    {
        Map<String, Object> replacements = new HashMap<>();

        for (String var : findVariables(string))
        {
            if (!definitions.hasVariable(var))
                throw new VaultCompilationException("The requested variable '"+var+"' could not be found in the container.");
            replacements.put("##"+var+"##", definitions.getVariable(var));
        }

        return replacements;
    }

    /**
     *
     * @param string
     * @return
     */
    public static Collection<String> findVariables(String string)
    {
        List<String> variables = new ArrayList<>();

        int prev = -1;
        for (int i = 0 ; (i = string.indexOf("##", i)) != -1 ; i += 2)
        {
            if (prev == -1)
                prev = i + 2;
            else {
                String var = string.substring(prev, i);
                variables.add(var);
                prev = -1;
            }
        }
        return variables;
    }

    /**
     *
     * @param string
     * @param definitions
     * @return
     */
    public static String replace(String string, Definitions definitions) throws VaultCompilationException
    {
        String newString = string;
        for (Map.Entry<String, Object> replacement : findReplacements(string, definitions).entrySet())
        {
            if (replacement.getValue() == null)
                continue;
            newString = newString.replace(replacement.getKey(), replacement.getValue().toString());
        }
        return newString;
    }

    /**
     *
     * @param params
     * @param definitions
     * @return
     */
    public static Object[] replaceParameters(Object[] params, Definitions definitions) throws VaultCompilationException
    {
        for (int i = 0 ; i < params.length ; ++i)
        {
            Object param = params[i];
            if (param instanceof String)
            {
                for (Map.Entry<String, Object> replacement : findReplacements((String)param, definitions).entrySet())
                {
                    /* If the sequence to be replaced is the whole parameter, we replace the parameter with the corresponding typed variable */
                    if (replacement.getKey().length() == ((String)param).length())
                        param = replacement.getValue();
                    /* Otherwise, the new parameter will always be a string */
                    else
                        param = ((String)param).replace(replacement.getKey(), replacement.getValue().toString());
                }
                params[i] = param;
            }
        }
        return params;
    }
}
