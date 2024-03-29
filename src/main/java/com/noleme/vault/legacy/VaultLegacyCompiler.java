package com.noleme.vault.legacy;

import com.noleme.vault.Provides;
import com.noleme.vault.Vault;
import com.noleme.vault.exception.RuntimeVaultException;
import com.noleme.vault.legacy.provider.CachedProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Qualifier;
import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/25
 */
@SuppressWarnings("rawtypes")
public class VaultLegacyCompiler
{
    private static final Logger logger = LoggerFactory.getLogger(VaultLegacyCompiler.class);

    /**
     *
     * @param target
     * @return
     */
    public static InjectableField[] injectFields(Class<?> target)
    {
        Set<Field> fields = fields(target);
        InjectableField[] injectableFields = new InjectableField[fields.size()];

        int i = 0;
        for (Field field : fields)
        {
            Class<?> providerType = field.getType().equals(Provider.class)
                ? (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]
                : null
            ;

            injectableFields[i++] = new InjectableField(
                field,
                providerType != null,
                Key.of(
                    (Class<?>)(providerType != null ? providerType : field.getType()),
                    qualifier(field.getAnnotations())
                )
            );
        }
        return injectableFields;
    }

    /**
     *
     * @param type
     * @return
     */
    public static Set<Field> fields(Class<?> type)
    {
        Class<?> current = type;
        Set<Field> fields = new HashSet<>();
        while (!current.equals(Object.class))
        {
            for (Field field : current.getDeclaredFields())
            {
                if (field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(javax.inject.Inject.class))
                {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
            current = current.getSuperclass();
        }
        return fields;
    }

    /**
     *
     * @param key
     * @return
     * @throws RuntimeVaultException
     */
    public static Constructor<?> constructor(Key key) throws RuntimeVaultException
    {
        Constructor<?> inject = null;
        Constructor<?> noarg = null;
        for (Constructor<?> c : key.type.getDeclaredConstructors())
        {
            if (c.isAnnotationPresent(Inject.class) || c.isAnnotationPresent(javax.inject.Inject.class))
            {
                if (inject == null)
                    inject = c;
                else
                    throw new RuntimeVaultException(String.format("%s has multiple @Inject constructors", key.type));
            }
            else if (c.getParameterTypes().length == 0)
                noarg = c;
        }

        Constructor<?> constructor = inject != null ? inject : noarg;
        if (constructor != null)
        {
            constructor.setAccessible(true);
            return constructor;
        }

        throw new RuntimeVaultException(String.format("%s doesn't have an @Inject or no-arg constructor, or a module provider", key.type.getName()));
    }

    /**
     *
     * @param annotations
     * @return
     */
    public static Annotation qualifier(Annotation[] annotations)
    {
        for (Annotation annotation : annotations)
        {
            if (annotation.annotationType().isAnnotationPresent(Qualifier.class) || annotation.annotationType().isAnnotationPresent(javax.inject.Qualifier.class))
                return annotation;
        }
        return null;
    }

    /**
     *
     * @param type
     * @return
     */
    public static Set<Method> providers(Class<?> type)
    {
        Class<?> current = type;

        Set<Method> providers = new HashSet<>();
        while (!current.equals(Object.class))
        {
            for (Method method : current.getDeclaredMethods())
            {
                if ((method.isAnnotationPresent(Provides.class) && (type.equals(current) || !providerInSubClass(method, providers))))
                {
                    method.setAccessible(true);
                    providers.add(method);
                }
            }
            current = current.getSuperclass();
        }
        return providers;
    }

    /**
     *
     * @param method
     * @param discoveredMethods
     * @return
     */
    public static boolean providerInSubClass(Method method, Set<Method> discoveredMethods)
    {
        for (Method discovered : discoveredMethods)
        {
            if (discovered.getName().equals(method.getName()) && Arrays.equals(method.getParameterTypes(), discovered.getParameterTypes()))
                return true;
        }
        return false;
    }

    /**
     *
     * @param vault
     * @param module
     * @param m
     */
    public static void providerMethod(Vault vault, final Object module, final Method m)
    {
        final Key<?> key = Key.of(m.getReturnType(), VaultLegacyCompiler.qualifier(m.getAnnotations()));
        if (vault.hasProvider(key))
            logger.debug("{} has multiple providers, module {}", key, module.getClass());

        final Provider<?>[] paramProviders = paramProviders(
            vault,
            key,
            m.getParameterTypes(),
            m.getGenericParameterTypes(),
            m.getParameterAnnotations(),
            Collections.singleton(key)
        );

        Provider<?> provider = () -> {
            try {
                return m.invoke(module, params(paramProviders));
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeVaultException(String.format("Can't instantiate %s with provider", key), e);
            }
        };

        vault.register(key, isProviderSingleton(m) ? new CachedProvider<>(provider) : provider, AutoCloseable.class.isAssignableFrom(m.getReturnType()));
    }

    /**
     *
     * @param m The provider method to be evaluated
     * @return true if either the method or its return type are annotated as singleton
     */
    private static boolean isProviderSingleton(final Method m)
    {
        if (m.getAnnotation(Singleton.class) != null || m.getAnnotation(javax.inject.Singleton.class) != null)
            return true;
        else if (m.getReturnType().getAnnotation(Singleton.class) != null || m.getReturnType().getAnnotation(javax.inject.Singleton.class) != null)
            return true;
        return false;
    }

    /**
     *
     * @param vault
     * @param key
     * @param parameterClasses
     * @param parameterTypes
     * @param annotations
     * @param chain
     * @return
     * @throws RuntimeVaultException
     */
    public static Provider<?>[] paramProviders(Vault vault, final Key key, Class<?>[] parameterClasses, Type[] parameterTypes, Annotation[][] annotations, final Set<Key> chain) throws RuntimeVaultException
    {
        Provider<?>[] providers = new Provider<?>[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; ++i)
        {
            Class<?> parameterClass = parameterClasses[i];
            Annotation qualifier = VaultLegacyCompiler.qualifier(annotations[i]);
            Class<?> providerType = Provider.class.equals(parameterClass)
                ? (Class<?>) ((ParameterizedType) parameterTypes[i]).getActualTypeArguments()[0]
                : null
            ;

            if (providerType == null)
            {
                final Key<?> newKey = Key.of(parameterClass, qualifier);
                final Set<Key> newChain = append(chain, key);
                if (newChain.contains(newKey))
                    throw new RuntimeVaultException(String.format("Circular dependency: %s", chain(newChain, newKey)));

                providers[i] = () -> vault.provider(newKey, newChain).get();
            }
            else {
                final Key<?> newKey = Key.of(providerType, qualifier);
                providers[i] = () -> vault.provider(newKey, null);
            }
        }
        return providers;
    }

    /**
     *
     * @param set
     * @param newKey
     * @return
     */
    public static Set<Key> append(Set<Key> set, Key newKey)
    {
        if (set != null && !set.isEmpty())
        {
            Set<Key> appended = new LinkedHashSet<>(set);
            appended.add(newKey);
            return appended;
        }

        return Collections.singleton(newKey);
    }

    /**
     *
     * @param chain
     * @param lastKey
     * @return
     */
    public static String chain(Set<Key> chain, Key lastKey)
    {
        StringBuilder chainString = new StringBuilder();

        for (Key key : chain)
            chainString.append(key.toString()).append(" -> ");

        return chainString.append(lastKey.toString()).toString();
    }

    /**
     *
     * @param paramProviders
     * @return
     */
    public static Object[] params(Provider<?>[] paramProviders)
    {
        Object[] params = new Object[paramProviders.length];
        for (int i = 0; i < paramProviders.length; ++i)
            params[i] = paramProviders[i].get();
        return params;
    }
}
