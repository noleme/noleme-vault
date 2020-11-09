package com.lumiomedical.vault;

import com.lumiomedical.vault.builder.BuildStage;
import com.lumiomedical.vault.builder.VaultBuilder;
import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.container.definition.Definitions;
import com.lumiomedical.vault.exception.RuntimeVaultException;
import com.lumiomedical.vault.exception.VaultException;
import com.lumiomedical.vault.legacy.Key;
import com.lumiomedical.vault.legacy.VaultLegacyCompiler;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 23/05/2020.
 * Adapted from org.codejargon.feather
 */
public final class Vault implements AutoCloseable
{
    private final Map<Key, Provider<?>> providers = new ConcurrentHashMap<>();
    private final Map<Key, Object> singletons = new ConcurrentHashMap<>();
    private final Map<String, Key> namedProviders = new ConcurrentHashMap<>();
    private final Map<Class, Object[][]> injectFields = new ConcurrentHashMap<>(0);
    private final List<AutoCloseable> enclosedCloseables = new ArrayList<>();

    /**
     *
     * @return
     */
    public static VaultBuilder builder()
    {
        return new VaultBuilder();
    }

    /**
     * Helper method for creating a simple one-stage Vault instance.
     *
     * @param stage
     * @return
     * @throws VaultException
     */
    public static Vault with(BuildStage stage) throws VaultException
    {
        return Vault.builder().with(stage).build();
    }

    /**
     * Helper method for creating a simple one-stage Vault instance.
     *
     * @param cellar
     * @return
     * @throws VaultException
     */
    public static Vault with(Cellar cellar) throws VaultException
    {
        return Vault.builder().with(cellar).build();
    }

    /**
     * Helper method for creating a simple one-stage Vault instance.
     *
     * @param definitions
     * @return
     * @throws VaultException
     */
    public static Vault with(Definitions definitions) throws VaultException
    {
        return Vault.builder().with(definitions).build();
    }

    /**
     * Helper method for creating a simple one-stage Vault instance.
     *
     * @param path
     * @return
     * @throws VaultException
     */
    public static Vault with(String path) throws VaultException
    {
        return Vault.builder().with(path).build();
    }

    /**
     * Helper method for creating a simple one-stage Vault instance.
     *
     * @param paths
     * @return
     * @throws VaultException
     */
    public static Vault with(String... paths) throws VaultException
    {
        return Vault.builder().with(List.of(paths)).build();
    }

    /**
     * Helper method for creating a simple one-stage Vault instance.
     *
     * @param path
     * @param adjuster
     * @return
     * @throws VaultException
     */
    public static Vault with(String path, Consumer<Definitions> adjuster) throws VaultException
    {
        return Vault.builder().with(path, adjuster).build();
    }

    /**
     * Helper method for creating a simple one-stage Vault instance.
     *
     * @param paths
     * @param adjuster
     * @return
     * @throws VaultException
     */
    public static Vault with(List<String> paths, Consumer<Definitions> adjuster) throws VaultException
    {
        return Vault.builder().with(paths, adjuster).build();
    }

    /**
     *
     * @param adjuster
     * @param paths
     * @return
     * @throws VaultException
     */
    public static Vault with(Consumer<Definitions> adjuster, String... paths) throws VaultException
    {
        return Vault.builder().with(adjuster, paths).build();
    }

    /**
     * Helper method for creating a simple one-stage Vault instance.
     *
     * @param modules
     * @return
     * @throws VaultException
     */
    public static Vault with(Object... modules) throws VaultException
    {
        return Vault.builder().with(modules).build();
    }

    public Vault() {}

    /**
     * @return an instance of type
     */
    public <T> T instance(Class<T> type)
    {
        return provider(Key.of(type), null).get();
    }

    /**
     * @return instance specified by key (type and qualifier)
     */
    public <T> T instance(Key<T> key)
    {
        return provider(key, null).get();
    }

    /**
     * @return provider of type
     */
    public <T> Provider<T> provider(Class<T> type)
    {
        return provider(Key.of(type), null);
    }

    /**
     * @return provider of key (type, qualifier)
     */
    public <T> Provider<T> provider(Key<T> key)
    {
        return provider(key, null);
    }

    /**
     * Injects fields to the target object
     *
     * @param target
     * @param <T>
     * @return
     * @throws VaultException
     */
    public <T> T inject(T target) throws VaultException
    {
        if (!this.injectFields.containsKey(target.getClass()))
            this.injectFields.put(target.getClass(), VaultLegacyCompiler.injectFields(target.getClass()));

        for (Object[] f : this.injectFields.get(target.getClass()))
        {
            Field field = (Field) f[0];
            Key<?> key = (Key<?>) f[2];

            try {
                field.set(target, (boolean) f[1] ? provider(key) : instance(key));
            }
            catch (IllegalAccessException e) {
                throw new VaultException(String.format("Can't inject field %s in %s", field.getName(), target.getClass().getName()), e);
            }
        }

        return target;
    }

    /**
     *
     * @param key
     * @param chain
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Provider<T> provider(final Key<T> key, Set<Key> chain) throws RuntimeVaultException
    {
        if (!this.providers.containsKey(key))
        {
            if (key.name != null && this.namedProviders.containsKey(key.name))
            {
                Key potentialParentKey = this.namedProviders.get(key.name);
                if (key.type.isAssignableFrom(potentialParentKey.type))
                    return (Provider<T>) this.providers.get(potentialParentKey);
            }
            if (key.name != null)
                throw new RuntimeVaultException("No service could be found for name "+key.name+".");

            final Constructor constructor = VaultLegacyCompiler.constructor(key);
            final Provider<?>[] paramProviders = VaultLegacyCompiler.paramProviders(
                this,
                key,
                constructor.getParameterTypes(),
                constructor.getGenericParameterTypes(),
                constructor.getParameterAnnotations(),
                chain
            );

            this.register(key, (Provider<?>) () -> {
                try {
                    var instance = constructor.newInstance(VaultLegacyCompiler.params(paramProviders));
                    return this.inject(instance);
                }
                catch (IllegalAccessException | InstantiationException | InvocationTargetException | VaultException e) {
                    throw new RuntimeVaultException(String.format("Can't instantiate %s", key.toString()), e);
                }
            });
        }
        return (Provider<T>) this.providers.get(key);
    }

    /**
     *
     * @param key
     * @param provider
     * @return
     */
    public Vault register(Key<?> key, Provider<?> provider)
    {
        return this.register(key, provider, false);
    }

    /**
     *
     * @param key
     * @param provider
     * @param closeable
     * @return
     */
    public Vault register(Key<?> key, Provider<?> provider, boolean closeable)
    {
        if (key.type.getAnnotation(Singleton.class) != null)
        {
            if (!this.singletons.containsKey(key))
            {
                synchronized (this.singletons)
                {
                    if (!this.singletons.containsKey(key))
                    {
                        Object instance = provider.get();
                        this.singletons.put(key, instance);
                        this.providers.put(key, () -> instance);
                        if (closeable)
                            this.enclosedCloseables.add((AutoCloseable) instance);
                    }
                }
            }
        }
        else {
            this.providers.put(key, provider);
            if (closeable)
                this.enclosedCloseables.add((AutoCloseable) provider.get());
        }

        if (key.name != null)
            this.namedProviders.put(key.name, key);

        return this;
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean hasProvider(Key<?> key)
    {
        return this.providers.containsKey(key);
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean hasSingleton(Key<?> key)
    {
        return this.singletons.containsKey(key);
    }

    @Override
    public void close() throws VaultException
    {
        try {
            for (AutoCloseable closeable : this.enclosedCloseables)
                closeable.close();
        }
        catch (Exception e) {
            throw new VaultException("An error occurred while attempting to close services declared as closeable.", e);
        }
    }
}
