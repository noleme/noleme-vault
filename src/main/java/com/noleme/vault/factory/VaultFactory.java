package com.noleme.vault.factory;

import com.noleme.commons.container.Pair;
import com.noleme.vault.container.Cellar;
import com.noleme.vault.container.Invocation;
import com.noleme.vault.container.definition.*;
import com.noleme.vault.exception.*;
import com.noleme.vault.parser.VaultCompositeParser;
import com.noleme.vault.parser.VaultParser;
import com.noleme.vault.reflect.LenientClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author Pierre Lecerf (pierre@noleme.com) on 13/09/2014.
 */
public class VaultFactory
{
    private final ClassLoader classLoader;
    private final VaultParser parser;
    public static VaultParser defaultParser = new VaultCompositeParser();

    private static final Logger logger = LoggerFactory.getLogger(VaultFactory.class);

    /**
     *
     */
    public VaultFactory()
    {
        this(defaultParser, null);
    }

    /**
     *
     * @param classLoader
     */
    public VaultFactory(ClassLoader classLoader)
    {
        this(defaultParser, classLoader);
    }

    /**
     *
     * @param parser
     */
    public VaultFactory(VaultParser parser)
    {
        this(parser, null);
    }

    /**
     *
     * @param parser
     * @param classLoader
     */
    public VaultFactory(VaultParser parser, ClassLoader classLoader)
    {
        this.parser = parser;
        this.classLoader = classLoader;
    }

    /**
     *
     * @param cellar
     * @param path
     * @return
     * @throws VaultInjectionException
     */
    public Cellar populate(Cellar cellar, String path) throws VaultInjectionException
    {
        try {
            Definitions definitions = this.parser().extract(path);

            return this.populate(cellar, definitions);
        }
        catch (VaultParserException e) {
            throw new VaultInjectionException("An error occurred while attempting to parse the provided configuration.", e);
        }
    }

    /**
     *
     * @param cellar
     * @param definitions
     * @return
     * @throws VaultInjectionException
     */
    public Cellar populate(Cellar cellar, Definitions definitions) throws VaultInjectionException
    {
        try {
            logger.debug("Populating cellar with definitions ({} service and {} variable definitions found)", definitions.services().size(), definitions.variables().size());

            Map<String, ServiceDefinition> definitionMap = this.computeDefinitionMap(definitions);

            this.checkCompleteness(definitionMap, cellar);
            List<ServiceDefinition> instantiations = this.checkStructure(definitionMap.values(), cellar);

            this.registerVariables(definitions, cellar);
            this.instantiate(instantiations, cellar);

            return cellar;
        }
        catch (VaultCompilationException e) {
            throw new VaultInjectionException("An error occurred while attempting to compile the object graph described by the configuration.", e);
        }
        catch (VaultInstantiationException e) {
            throw new VaultInjectionException("An error occurred while attempting to build services.", e);
        }
    }

    /**
     *
     * @return
     */
    public VaultParser parser()
    {
        return this.parser;
    }

    /**
     *
     * @param definitions
     * @return
     * @throws VaultCompilationException
     */
    private Map<String, ServiceDefinition> computeDefinitionMap(Definitions definitions) throws VaultCompilationException
    {
        Map<String, ServiceDefinition> map = new HashMap<>();

        for (ServiceDefinition def : definitions.services().values())
            map.put(def.getIdentifier(), def);

        for (String identifier : definitions.tags().identifiers())
        {
            if (definitions.services().has(identifier) && !(definitions.services().get(identifier) instanceof ServiceTag))
                throw new VaultCompilationException("Tag identifier "+identifier+" is in conflict with service definition "+definitions.services().get(identifier).toString());

            var serviceTag = definitions.services().has(identifier)
                ? (ServiceTag) definitions.services().get(identifier)
                : new ServiceTag(identifier)
            ;

            for (Tag tag : definitions.tags().forIdentifier(identifier))
                serviceTag.addEntry(tag.getService());

            map.put(identifier, serviceTag);
        }

        return map;
    }

    /**
     *
     * @param definitions
     * @throws VaultCompilationException
     */
    private void checkCompleteness(Map<String, ServiceDefinition> definitions, Cellar cellar) throws VaultCompilationException
    {
        for (Map.Entry<String, ServiceDefinition> defEntry : definitions.entrySet())
        {
            ServiceDefinition definition = defEntry.getValue();
            for (String dependency : definition.getDependencies())
            {
                if (!definitions.containsKey(dependency) && !cellar.hasService(dependency))
                    throw new VaultCompilationException("The service "+definition.getIdentifier()+" has a dependency over a non-existing "+dependency+" service.");
            }
        }
    }

    /**
     * Implementation of a topological sort preparing services for instantiation.
     *
     * @param definitionsSource
     * @param cellar
     * @return
     * @throws VaultCompilationException
     */
    private List<ServiceDefinition> checkStructure(Collection<ServiceDefinition> definitionsSource, Cellar cellar) throws VaultCompilationException
    {
        List<ServiceDefinition> definitions = new ArrayList<>(definitionsSource);
        List<ServiceDefinition> instantiations = new ArrayList<>(definitions.size());

        Queue<ServiceDefinition> processQueue = new LinkedList<>();
        Iterator<ServiceDefinition> it = definitions.iterator();
        while (it.hasNext())
        {
            ServiceDefinition def = it.next();
            if (def.getDependencies().isEmpty())
            {
                processQueue.add(def);
                it.remove();
            }
            /*
             * If the service has dependencies, but they are already satisfied by the current container, we ignore these
             * dependencies from the structure check.
             */
            else {
                boolean isSatisfiedByContainer = true;
                for (String dependencyIdentifier : def.getDependencies())
                {
                    if (!cellar.hasService(dependencyIdentifier))
                        isSatisfiedByContainer = false;
                }
                if (isSatisfiedByContainer)
                {
                    processQueue.add(def);
                    it.remove();
                }
            }
        }

        while (!processQueue.isEmpty())
        {
            ServiceDefinition def = processQueue.poll();
            instantiations.add(def);

            var depServiceIterator = definitions.iterator();
            while (depServiceIterator.hasNext())
            {
                ServiceDefinition dependentService = depServiceIterator.next();

                dependentService
                    .getDependencies()
                    .removeIf(s -> s.equals(def.getIdentifier()))
                ;

                if (dependentService.getDependencies().isEmpty())
                {
                    processQueue.add(dependentService);
                    depServiceIterator.remove();
                }
            }
        }
        if (!definitions.isEmpty())
            throw new VaultCompilationException("The Service definition contains a circular reference involving "+definitions.size()+" entit"+(definitions.size() > 1 ? "ies" : "y")+" : "+getString(definitions)+".");

        return instantiations;
    }

    /**
     *
     * @param definitions
     * @return
     */
    private static String getString(Collection<ServiceDefinition> definitions)
    {
        StringJoiner joiner = new StringJoiner(",");

        for (ServiceDefinition def : definitions)
            joiner.add(def.getIdentifier());

        return joiner.toString();
    }

    /**
     *
     * @param definitions
     * @param cellar
     */
    private void registerVariables(Definitions definitions, Cellar cellar)
    {
        definitions.variables().dictionary().forEach(cellar::putVariable);
    }

    /**
     *
     * @param definitions
     * @param cellar
     * @throws VaultInstantiationException
     */
    private void instantiate(List<ServiceDefinition> definitions, Cellar cellar) throws VaultInstantiationException
    {
        try {
            for (ServiceDefinition def : definitions)
            {
                if (def instanceof ServiceAlias)
                    cellar.putService(def.getIdentifier(), cellar.getService(((ServiceAlias)def).getTarget()));
                else if (def instanceof ServiceInstantiation)
                {
                    Object instance = this.makeInstantiation((ServiceInstantiation)def, cellar);
                    this.makeInvocations(def, instance, cellar);
                    cellar.putService(def.getIdentifier(), instance);

                    if (((ServiceInstantiation) def).isCloseable())
                    {
                        if (!(instance instanceof AutoCloseable))
                            throw new VaultInstantiationException("The \""+def.getIdentifier()+"\" service was marked as closeable but its implementation is not a java.lang.AutoCloseable subtype.");
                        cellar.registerCloseable(def.getIdentifier());
                    }
                }
                else if (def instanceof ServiceProvider)
                {
                    Object instance = this.makeStaticCall((ServiceProvider)def, cellar);
                    this.makeInvocations(def, instance, cellar);
                    cellar.putService(def.getIdentifier(), instance);

                    if (((ServiceProvider) def).isCloseable())
                    {
                        if (!(instance instanceof AutoCloseable))
                            throw new VaultInstantiationException("The \""+def.getIdentifier()+"\" service was marked as closeable but its implementation is not a java.lang.AutoCloseable subtype.");
                        cellar.registerCloseable(def.getIdentifier());
                    }
                }
                else if (def instanceof ServiceTag)
                {
                    List<String> entriesIds = ((ServiceTag) def).getEntries();

                    List<Object> collection = new ArrayList<>(entriesIds.size());
                    for (String entryId : entriesIds)
                        collection.add(cellar.getService(entryId));

                    cellar.putService(def.getIdentifier(), collection);
                }
            }
        }
        catch (VaultNotFoundException e) {
            throw new VaultInstantiationException("An error occurred while attempting to retrieve an alias target.", e);
        }
    }

    /**
     *
     * @param definition
     * @param cellar
     * @return
     * @throws VaultInstantiationException
     */
    private Object makeInstantiation(ServiceInstantiation definition, Cellar cellar) throws VaultInstantiationException
    {
        try {
            Class<?> c = this.classLoader != null
                ? Class.forName(definition.getType(), true, this.classLoader)
                : Class.forName(definition.getType())
            ;

            Object[] params = definition.getCtorParams();
            Class<?>[] paramTypes = new Class[params.length];
            for (int i = 0; i < params.length; ++i)
            {
                Object o = params[i];
                if (o instanceof String && ((String)o).startsWith("@"))
                {
                    String id = ((String)o).substring(1);
                    o = cellar.getService(id);
                    params[i] = o;
                }
                paramTypes[i] = o != null ? o.getClass() : null;
            }

            Pair<Constructor<?>, Object[]> resolvedCtor = LenientClassUtils.getLenientConstructor(c, paramTypes, params);
            Constructor<?> ctor = resolvedCtor.first;
            params = resolvedCtor.second;

            return ctor.newInstance(params);
        }
        catch (ClassNotFoundException e) {
            throw new VaultInstantiationException("Class type "+definition.getType()+" could not be instantiated (ClassNotFoundException)", e);
        }
        catch (NoSuchMethodException e) {
            throw new VaultInstantiationException("The expected constructor could not be found for service \""+definition.getIdentifier()+"\". (NoSuchMethodException)", e);
        }
        catch (InvocationTargetException e) {
            throw new VaultInstantiationException("The constructor for service \""+definition.getIdentifier()+"\" has thrown an exception. (InvocationTargetException)", e);
        }
        catch (InstantiationException e) {
            throw new VaultInstantiationException("Service \""+definition.getIdentifier()+"\" of type "+definition.getType()+" could not be instantiated. (InstantiationException)", e);
        }
        catch (IllegalArgumentException e) {
            throw new VaultInstantiationException("Constructor for service \""+definition.getIdentifier()+"\" cannot accept the provided arguments for invocation. (IllegalArgumentException)", e);
        }
        catch (IllegalAccessException e) {
            throw new VaultInstantiationException("Constructor in service \""+definition.getIdentifier()+"\" is not accessible for invocation. (IllegalAccessException)", e);
        }
        catch (VaultNotFoundException e) {
            throw new VaultInstantiationException("An unexpected error occurred: a dependency for service \""+definition.getIdentifier()+"\" instantiation could not be found within the service definition map.", e);
        }
    }

    /**
     *
     * @param definition
     * @param cellar
     * @return
     * @throws VaultInstantiationException
     */
    private Object makeStaticCall(ServiceProvider definition, Cellar cellar) throws VaultInstantiationException
    {
        try {
            Class<?> c = this.classLoader != null
                ? Class.forName(definition.getType(), true, this.classLoader)
                : Class.forName(definition.getType())
            ;

            String methodName = definition.getMethod();
            Object[] args = definition.getMethodArgs();
            Class<?>[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; ++i)
            {
                Object o = args[i];
                if (o instanceof String && ((String)o).startsWith("@"))
                {
                    String id = ((String)o).substring(1);
                    o = cellar.getService(id);
                    args[i] = o;
                }
                argTypes[i] = o != null ? o.getClass() : null;
            }

            Pair<Method, Object[]> resolvedCtor = LenientClassUtils.getLenientMethod(c, methodName, argTypes, args);
            Method method = resolvedCtor.first;
            args = resolvedCtor.second;

            if (!Modifier.isStatic(method.getModifiers()))
                throw new VaultInstantiationException("The provider specified for service \""+definition.getIdentifier()+"\" has to be a static method.");

            return method.invoke(null, args);
        }
        catch (ClassNotFoundException e) {
            throw new VaultInstantiationException("The provider method enclosing class of type "+definition.getType()+" could not be found (ClassNotFoundException)", e);
        }
        catch (NoSuchMethodException e) {
            throw new VaultInstantiationException("The provider method could not be found in class "+definition.getType()+" for service \""+definition.getIdentifier()+"\". (NoSuchMethodException) Make sure the method is not private.", e);
        }
        catch (InvocationTargetException e) {
            throw new VaultInstantiationException("The provider method for service \""+definition.getIdentifier()+"\" has thrown an exception. (InvocationTargetException)", e);
        }
        catch (IllegalArgumentException e) {
            throw new VaultInstantiationException("Provider method for service \""+definition.getIdentifier()+"\" cannot accept the provided arguments for invocation. (IllegalArgumentException)", e);
        }
        catch (IllegalAccessException e) {
            throw new VaultInstantiationException("Provider method for service \""+definition.getIdentifier()+"\" is not accessible for invocation. (IllegalAccessException)", e);
        }
        catch (VaultNotFoundException e) {
            throw new VaultInstantiationException("An unexpected error occurred: a dependency for service \""+definition.getIdentifier()+"\" instantiation could not be found within the provided service cellar.", e);
        }
    }

    /**
     *
     * @param definition
     * @param instance
     * @param cellar
     * @throws VaultInstantiationException
     */
    private void makeInvocations(ServiceDefinition definition, Object instance, Cellar cellar) throws VaultInstantiationException
    {
        try {
            for (Invocation invocation : definition.getInvocations())
            {
                Object[] params = invocation.getParams();
                Class<?>[] paramTypes = new Class[params.length];
                for (int i = 0 ; i < params.length ; ++i)
                {
                    Object o = params[i];
                    if (o instanceof String && ((String)o).startsWith("@"))
                    {
                        String id = ((String)o).substring(1);
                        o = cellar.getService(id);
                        params[i] = o;
                    }
                    paramTypes[i] = o != null ? o.getClass() : null;
                }

                Pair<Method, Object[]> resolvedCtor = LenientClassUtils.getLenientMethod(instance.getClass(), invocation.getMethodName(), paramTypes, params);
                Method method = resolvedCtor.first;
                params = resolvedCtor.second;

                method.invoke(instance, params);
            }
        }
        catch (NoSuchMethodException e) {
            throw new VaultInstantiationException("No invocation target method could be found for service \""+definition.getIdentifier()+"\". (NoSuchMethodException)", e);
        }
        catch (IllegalAccessException e) {
            throw new VaultInstantiationException("A method in service \""+definition.getIdentifier()+"\" is not accessible for invocation. (IllegalAccessException)", e);
        }
        catch (IllegalArgumentException e) {
            throw new VaultInstantiationException("A method in service \""+definition.getIdentifier()+"\" cannot accept the provided arguments for invocation. (IllegalArgumentException)", e);
        }
        catch (InvocationTargetException e) {
            throw new VaultInstantiationException("An invocation on service \""+definition.getIdentifier()+"\" has thrown an exception. (InvocationTargetException)", e);
        }
        catch (VaultNotFoundException e) {
            throw new VaultInstantiationException("An unexpected error occurred: a dependency for service \""+definition.getIdentifier()+"\" invocations could not be found within the provided service cellar.", e);
        }
    }
}
