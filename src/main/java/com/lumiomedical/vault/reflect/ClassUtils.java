package com.lumiomedical.vault.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/11/27
 */
public final class ClassUtils
{
    private ClassUtils() {}

    /**
     * Attempts to find a constructor for the provided class matching with the provided parameter types.
     *
     * @param type the type of the class on which to perform the lookup
     * @param parameterTypes the types of arguments expected for the constructor signature
     * @return a matching Constructor instance
     * @throws NoSuchMethodException thrown if no matching constructor could be found
     */
    public static Constructor getConstructor(Class type, Class[] parameterTypes) throws NoSuchMethodException
    {
        CTOR_LOOP: for (Constructor ctor : type.getConstructors())
        {
            if (ctor.getParameterTypes().length != parameterTypes.length)
                continue;
            Class[] ctorParameterTypes = ctor.getParameterTypes();
            for (int p = 0 ; p < ctorParameterTypes.length ; ++p)
            {
                Class ctorParameterType = ctorParameterTypes[p];
                Class parameterType = parameterTypes[p];

                if (!matchesArgumentType(parameterType, ctorParameterType))
                    continue CTOR_LOOP;
            }
            return ctor;
        }
        throw new NoSuchMethodException(type.getName()+".<init>("+ Arrays.toString(parameterTypes)+")");
    }

    /**
     * Attempts to find a method for the provided class matching with the provided parameter types.
     *
     * @param type the type of the class on which to perform the lookup
     * @param methodName the name of the method
     * @param parameterTypes the types of the arguments expected for the method signature
     * @return a matching Method instance
     * @throws NoSuchMethodException thrown if no matching method could be found
     */
    public static Method getMethod(Class type, String methodName, Class[] parameterTypes) throws NoSuchMethodException
    {
        METHOD_LOOP: for (Method method : type.getMethods())
        {
            if (!method.getName().equals(methodName))
                continue;
            if (method.getParameterTypes().length != parameterTypes.length)
                continue;
            Class[] methodParameterTypes = method.getParameterTypes();
            for (int p = 0 ; p < methodParameterTypes.length ; ++p)
            {
                Class methodParameterType = methodParameterTypes[p];
                Class parameterType = parameterTypes[p];

                if (!matchesArgumentType(parameterType, methodParameterType))
                    continue METHOD_LOOP;
            }
            return method;
        }
        throw new NoSuchMethodException(type.getName()+"."+methodName+"("+Arrays.toString(parameterTypes)+")");
    }

    /**
     * Checks whether a given type "matches" with another type found in a method/ctor signature.
     *
     * @param providedType the type of an input
     * @param argumentType the type found in a method/ctor signature
     * @return true if the types are compatible, false otherwise
     */
    private static boolean matchesArgumentType(Class<?> providedType, Class<?> argumentType)
    {
        if (providedType == null)
        {
            if (argumentType.isPrimitive())
                return false;
            return true;
        }
        if (argumentType.isAssignableFrom(providedType))
            return true;
        if (isBoxedTypeFor(providedType, argumentType))
            return true;
        return false;
    }

    /**
     *
     * @param c1
     * @param c2
     * @return
     */
    private static boolean isBoxedTypeFor(Class c1, Class c2)
    {
        if (!c2.isPrimitive())
            return false;
        return (
            (c1.equals(Boolean.class) && c2.equals(boolean.class))
            || (c1.equals(Integer.class) && c2.equals(int.class))
            || (c1.equals(Long.class) && c2.equals(long.class))
            || (c1.equals(Double.class) && c2.equals(double.class))
            || (c1.equals(Float.class) && c2.equals(float.class))
            || (c1.equals(Byte.class) && c2.equals(byte.class))
            || (c1.equals(Short.class) && c2.equals(short.class))
            || (c1.equals(Character.class) && c2.equals(char.class))
        );
    }
}
