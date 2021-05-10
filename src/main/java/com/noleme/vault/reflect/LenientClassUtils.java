package com.noleme.vault.reflect;

import com.noleme.commons.container.Pair;
import com.noleme.vault.exception.VaultInvalidTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 27/04/2021
 */
public final class LenientClassUtils
{
    private static final Logger logger = LoggerFactory.getLogger(LenientClassUtils.class);
    private static final Set<Class<?>> conversionTargets = Set.of(
        Boolean.class, Integer.class, Long.class, Float.class, Double.class, Byte.class, Short.class, Character.class,
        boolean.class, int.class, long.class, float.class, double.class, byte.class, short.class, char.class
    );

    private LenientClassUtils() {}

    /**
     * Attempts to find a constructor for the provided class matching with the provided parameter types, if the search fails, it will attempt to make type conversions from String arguments.
     *
     * @param type the type of the class on which to perform the lookup
     * @param parameterTypes the types of arguments expected for the constructor signature
     * @param parameters the original expected arguments for the constructor
     * @return a matching Constructor instance paired with a functional set of arguments (with possible type re-interpretations)
     * @throws NoSuchMethodException thrown if no matching constructor could be found and/or type conversions failed
     */
    public static Pair<Constructor<?>, Object[]> getLenientConstructor(Class<?> type, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException
    {
        /* We first try to find the constructor using regular means */
        try {
            return new Pair<>(ClassUtils.getConstructor(type, parameterTypes), parameters);
        }
        /* If it fails, we attempt type conversions */
        catch (NoSuchMethodException e) {
            /* First, convertible arguments (ie. String arguments) are indexed */
            Set<Integer> convertibleParametersIndexes = indexConvertibleArguments(parameters);

            /* Then, we perform a constructor lookup in order to find signatures where we can convert non-matching types using one or several of the indexed convertible arguments */
            CTOR_LOOP: for (Constructor<?> ctor : type.getConstructors())
            {
                if (ctor.getParameterTypes().length != parameterTypes.length)
                    continue;

                Map<Integer, Class<?>> convertibleArguments = new HashMap<>();

                Class<?>[] ctorParameterTypes = ctor.getParameterTypes();
                for (int p = 0 ; p < ctorParameterTypes.length ; ++p)
                {
                    Class<?> ctorParameterType = ctorParameterTypes[p];
                    Class<?> parameterType = parameterTypes[p];

                    /* Either the argument type already matches, or the type is one we can attempt to convert to */
                    if (ClassUtils.matchesArgumentType(parameterType, ctorParameterType))
                        continue;
                    else if (isConversionTarget(ctorParameterType) && convertibleParametersIndexes.contains(p))
                        convertibleArguments.put(p, ctorParameterType);
                    else
                        continue CTOR_LOOP;
                }

                /* If we reach that point, it means we found a good type conversion candidate where we can attempt type conversions for recovering missing arguments */
                var lenientParams = computeLenientArguments(convertibleArguments, parameters);

                if (lenientParams != null)
                {
                    logger.debug("Found lenient constructor {} making type conversions over arguments {}", ctor.toGenericString(), convertibleArguments.keySet());
                    return new Pair<>(ctor, lenientParams);
                }

                continue CTOR_LOOP;
            }

            throw e;
        }
    }

    /**
     * Attempts to find a method for the provided class matching with the provided parameter types.
     *
     * @param type the type of the class on which to perform the lookup
     * @param methodName the name of the method
     * @param parameterTypes the types of the arguments expected for the method signature
     * @param parameters the original expected arguments for the method
     * @return a matching Method instance paired with a functional set of arguments (with possible type re-interpretations)
     * @throws NoSuchMethodException thrown if no matching method could be found and/or type conversions failed
     */
    public static Pair<Method, Object[]> getLenientMethod(Class<?> type, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws NoSuchMethodException
    {
        /* We first try to find the method using regular means */
        try {
            return new Pair<>(ClassUtils.getMethod(type, methodName, parameterTypes), parameters);
        }
        /* If it fails, we attempt type conversions */
        catch (NoSuchMethodException e) {
            /* First, convertible arguments (ie. String arguments) are indexed */
            Set<Integer> convertibleParametersIndexes = indexConvertibleArguments(parameters);

            /* Then, we perform a method lookup in order to find signatures where we can convert non-matching types using one or several of the indexed convertible arguments */
            METHOD_LOOP: for (Method method : type.getMethods())
            {
                if (!method.getName().equals(methodName))
                    continue;
                if (method.getParameterTypes().length != parameterTypes.length)
                    continue;

                Map<Integer, Class<?>> convertibleArguments = new HashMap<>();

                Class<?>[] methodParameterTypes = method.getParameterTypes();
                for (int p = 0 ; p < methodParameterTypes.length ; ++p)
                {
                    Class<?> methodParameterType = methodParameterTypes[p];
                    Class<?> parameterType = parameterTypes[p];

                    /* Either the argument type already matches, or the type is one we can attempt to convert to */
                    if (ClassUtils.matchesArgumentType(parameterType, methodParameterType))
                        continue;
                    else if (isConversionTarget(methodParameterType) && convertibleParametersIndexes.contains(p))
                        convertibleArguments.put(p, methodParameterType);
                    else
                        continue METHOD_LOOP;
                }

                /* If we reach that point, it means we found a good type conversion candidate where we can attempt type conversions for recovering missing arguments */
                var lenientArgs = computeLenientArguments(convertibleArguments, parameters);

                if (lenientArgs != null)
                {
                    logger.debug("Found lenient method {} making type conversions over arguments {}", method.toGenericString(), convertibleArguments.keySet());
                    return new Pair<>(method, lenientArgs);
                }

                continue METHOD_LOOP;
            }

            throw e;
        }
    }

    /**
     *
     * @param parameters
     * @return
     */
    private static Set<Integer> indexConvertibleArguments(Object[] parameters)
    {
        Set<Integer> convertibleArgumentsIndexes = new HashSet<>();
        for (int i = 0 ; i < parameters.length ; ++i)
        {
            if (parameters[i] instanceof String)
                convertibleArgumentsIndexes.add(i);
        }
        return convertibleArgumentsIndexes;
    }

    /**
     *
     * @param convertibleArguments
     * @param parameters
     * @return
     */
    private static Object[] computeLenientArguments(Map<Integer, Class<?>> convertibleArguments, Object[] parameters)
    {
        try {
            Object[] lenientParameters = new Object[parameters.length];
            for (int p = 0 ; p < parameters.length ; ++p)
            {
                Object lenientParameter = parameters[p];

                if (convertibleArguments.containsKey(p))
                {
                    Class<?> targetType = convertibleArguments.get(p);
                    /* If the conversion is successful, we replace the original argument with our conversion */
                    lenientParameter = attemptTypeConversion((String) parameters[p], targetType);
                }

                lenientParameters[p] = lenientParameter;
            }
            return lenientParameters;
        }
        /* If the type conversion fails, we'll look for another candidate */
        catch (VaultInvalidTypeException ite) {
            logger.debug("An attempt at converting types in order to match arguments failed: {}", ite.getMessage());
            return null;
        }
    }

    /**
     *
     * @param value
     * @param type
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T attemptTypeConversion(String value, Class<T> type)
    {
        logger.debug("Attempting type conversion for string \"{}\" to type {}", value, type.getName());

        try {
            if (type.equals(String.class))
                return (T) value;
            if ((type.equals(Boolean.class) || type.equals(boolean.class)) && ClassUtils.isParseableAsBoolean(value))
                return (T) Boolean.valueOf(Boolean.parseBoolean(value));
            if (type.equals(Integer.class) || type.equals(int.class))
                return (T) Integer.valueOf(Integer.parseInt(value));
            if (type.equals(Long.class) || type.equals(long.class))
                return (T) Long.valueOf(Long.parseLong(value));
            if (type.equals(Double.class) || type.equals(double.class))
                return (T) Double.valueOf(Double.parseDouble(value));
            if (type.equals(Float.class) || type.equals(float.class))
                return (T) Float.valueOf(Float.parseFloat(value));
            if (type.equals(Byte.class) || type.equals(byte.class))
                return (T) Byte.decode(value);
            if (type.equals(Short.class) || type.equals(short.class))
                return (T) Short.valueOf(Short.parseShort(value));
            if (type.equals(Character.class) || type.equals(char.class))
            {
                if (value.length() == 1)
                    return (T) Character.valueOf(value.charAt(0));
            }
        }
        catch (NumberFormatException e) {
            throw new VaultInvalidTypeException("Value \""+value+"\" could not be converted to type "+type.getName(), e);
        }
        throw new VaultInvalidTypeException("Value \""+value+"\" could not be converted to type "+type.getName());
    }

    /**
     *
     * @param type
     * @return
     */
    public static boolean isConversionTarget(Class<?> type)
    {
        return conversionTargets.contains(type);
    }
}
