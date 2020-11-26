package com.lumiomedical.vault.factory;

import com.lumiomedical.vault.container.Cellar;
import com.lumiomedical.vault.container.Invocation;
import com.lumiomedical.vault.container.definition.*;
import com.lumiomedical.vault.container.definition.hook.PostResolvingHandler;
import com.lumiomedical.vault.exception.*;
import com.lumiomedical.vault.parser.VaultFlexibleParser;
import com.lumiomedical.vault.parser.VaultParser;

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
    private final VaultParser parser;
    private final ClassLoader classLoader;
    public static VaultParser defaultParser = new VaultFlexibleParser();

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
            Definitions definitions = this.parse(path);

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
            this.resolveVariables(definitions);

            for (PostResolvingHandler handler : definitions.getHandlers())
                handler.handle(definitions);

            this.checkCompleteness(definitions, cellar);
            List<ServiceDefinition> instantiations = this.checkStructure(definitions.listDefinitions(), cellar);

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
     * @param path
     * @return
     * @throws VaultParserException
     */
    public Definitions parse(String path) throws VaultParserException
    {
        return this.parse(path, new Definitions());
    }

    /**
     *
     * @param path
     * @param definitions
     * @return
     * @throws VaultParserException
     */
    public Definitions parse(String path, Definitions definitions) throws VaultParserException
    {
        if (path == null || path.isEmpty())
            throw new VaultParserException("The provided path was empty or null and thus cannot be processed.");

        return this.parser.extract(path, definitions);
    }

    /**
     * Will resolve the 'actual' value of each variable in the provided Definitions instance by checking for references to other variables.
     * Once this is done, we should have a set of "final" variables that can be searched and replaced within service definitions.
     *
     * @param definitions
     * @throws VaultCompilationException
     */
    private void resolveVariables(Definitions definitions) throws VaultCompilationException
    {
        Map<String, Variable> variables = new HashMap<>();
        Queue<Variable> queue = new LinkedList<>();
        List<Variable> heap = new ArrayList<>();
        List<String> sorted = new ArrayList<>();

        for (Map.Entry<String, Object> varDef : definitions.getVariables().entrySet())
        {
            Variable var = new Variable(varDef.getKey(), varDef.getValue());
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
            throw new VaultCompilationException("The variable definitions contain a circular reference involving "+heap.size()+" variables.");

        for (String vk : sorted)
        {
            Variable v = variables.get(vk);

            if (!(v.getValue() instanceof String))
                continue;

            String val = (String)v.getValue();
            for (String dep : v.getDependencies())
            {
                var value = variables.get(dep).getValue();
                val = val.replace("##"+dep+"##", value != null ? value.toString() : "");
            }
            val = Variable.replaceEnv(val);

            v.setValue(val);
            definitions.setVariable(vk, val);
        }

        this.replaceVariables(definitions);
    }

    /**
     * Will inspect all service definitions in the provided Definitions instance and replace variable references by their actual value.
     *
     * @param definitions
     * @throws VaultCompilationException
     */
    private void replaceVariables(Definitions definitions) throws VaultCompilationException
    {
        Map<String, ServiceDefinition> movedDefinitions = new HashMap<>();

        try {
            Iterator<ServiceDefinition> it = definitions.listDefinitions().iterator();
            while (it.hasNext())
            {
                ServiceDefinition def = it.next();

                /* Service identifier replacements */
                String newId = Variable.replace(def.getIdentifier(), definitions);
                if (!newId.equals(def.getIdentifier()))
                {
                    it.remove();
                    def.setIdentifier(newId);
                    movedDefinitions.put(newId, def);
                }

                /* Service invocation replacements */
                for (Invocation invocation : def.getInvocations())
                    invocation.setParams(Variable.replaceParameters(invocation.getParams(), definitions));

                /* Service parameter replacements */
                if (def instanceof ServiceInstantiation)
                {
                    ServiceInstantiation instantiation = ((ServiceInstantiation)def);
                    instantiation.setType(Variable.replace(instantiation.getType(), definitions));
                    instantiation.setCtorParams(Variable.replaceParameters(instantiation.getCtorParams(), definitions));
                }
                if (def instanceof ServiceProvider)
                {
                    ServiceProvider provider = ((ServiceProvider)def);
                    provider.setType(Variable.replace(provider.getType(), definitions));
                    provider.setMethod(Variable.replace(provider.getMethod(), definitions));
                    provider.setMethodArgs(Variable.replaceParameters(provider.getMethodArgs(), definitions));
                }

                def.syncDependencies();
            }

            for (Map.Entry<String, ServiceDefinition> moved : movedDefinitions.entrySet())
                definitions.setDefinition(moved.getKey(), moved.getValue());
        }
        catch (VaultCompilationException e) {
            throw new VaultCompilationException("An error occurred while attempting to compile a variable.", e);
        }
    }

    /**
     *
     * @param definitions
     * @throws VaultCompilationException
     */
    private void checkCompleteness(Definitions definitions, Cellar cellar) throws VaultCompilationException
    {
        for (ServiceDefinition definition : definitions.listDefinitions())
        {
            for (String dependency : definition.getDependencies())
            {
                if (!definitions.hasDefinition(dependency) && !cellar.hasService(dependency))
                    throw new VaultCompilationException("The service "+definition.getIdentifier()+" has a dependency over a non-existing "+dependency+" service.");
            }
        }
    }

    /**
     * Implementation of a topological sort preparing services for instantiation.
     *
     * @param definitions
     * @param cellar
     * @return
     * @throws VaultCompilationException
     */
    private List<ServiceDefinition> checkStructure(Collection<ServiceDefinition> definitions, Cellar cellar) throws VaultCompilationException
    {
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
            Iterator<ServiceDefinition> depService_it = definitions.iterator();
            while (depService_it.hasNext())
            {
                ServiceDefinition dependentService = depService_it.next();
                Iterator<String> dependency_it = dependentService.getDependencies().iterator();
                while (dependency_it.hasNext())
                {
                    if (dependency_it.next().equals(def.getIdentifier()))
                        dependency_it.remove();
                }
                if (dependentService.getDependencies().isEmpty())
                {
                    processQueue.add(dependentService);
                    depService_it.remove();
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
        for (Map.Entry<String, Object> e : definitions.getVariables().entrySet())
            cellar.putVariable(e.getKey(), e.getValue());
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
            Class c = this.classLoader != null
                ? Class.forName(definition.getType(), true, this.classLoader)
                : Class.forName(definition.getType())
            ;

            Object[] params = definition.getCtorParams();
            Class[] paramTypes = new Class[params.length];
            for (int i = 0; i < params.length; ++i)
            {
                Object o = params[i];
                if (o instanceof String && ((String)o).startsWith("@"))
                {
                    String id = ((String)o).substring(1);
                    o = cellar.getService(id);
                    params[i] = o;
                }
                paramTypes[i] = o.getClass();
            }
            Constructor ctor = getConstructor(c, paramTypes);
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
            Class c = this.classLoader != null
                ? Class.forName(definition.getType(), true, this.classLoader)
                : Class.forName(definition.getType())
            ;

            String methodName = definition.getMethod();
            Object[] args = definition.getMethodArgs();
            Class[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; ++i)
            {
                Object o = args[i];
                if (o instanceof String && ((String)o).startsWith("@"))
                {
                    String id = ((String)o).substring(1);
                    o = cellar.getService(id);
                    args[i] = o;
                }
                argTypes[i] = o.getClass();
            }
            Method method = getMethod(c, methodName, argTypes);

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
                Class[] paramTypes = new Class[params.length];
                for (int i = 0 ; i < params.length ; ++i)
                {
                    Object o = params[i];
                    if (o instanceof String && ((String)o).startsWith("@"))
                    {
                        String id = ((String)o).substring(1);
                        o = cellar.getService(id);
                        params[i] = o;
                    }
                    paramTypes[i] = o.getClass();
                }

                Method method = getMethod(instance.getClass(), invocation.getMethodName(), paramTypes);
                method.invoke(instance, params);
            }
        }
        catch (NoSuchMethodException e) {
            throw new VaultInstantiationException("No invocation target method could be found for service \""+definition.getIdentifier()+"\". (NoSuchMethodException)", e);
        }
        catch (IllegalAccessException e) {
            throw new VaultInstantiationException("A method in service \""+definition.getIdentifier()+"\" is not accessible for invocation. (IllegalAccessException)", e);
        }
        catch (InvocationTargetException e) {
            throw new VaultInstantiationException("An invocation on service \""+definition.getIdentifier()+"\" has thrown an exception. (InvocationTargetException)", e);
        }
        catch (VaultNotFoundException e) {
            throw new VaultInstantiationException("An unexpected error occurred: a dependency for service \""+definition.getIdentifier()+"\" invocations could not be found within the provided service cellar.", e);
        }
    }

    /**
     *
     * @param type
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    @SuppressWarnings("unchecked")
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
                if (!ctorParameterType.isAssignableFrom(parameterType) && !isPrimitive(parameterType, ctorParameterType))
                    continue CTOR_LOOP;
            }
            return ctor;
        }
        throw new NoSuchMethodException(type.getName()+".<init>("+Arrays.toString(parameterTypes)+")");
    }

    /**
     *
     * @param type
     * @param methodName
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    @SuppressWarnings("unchecked")
    public static Method getMethod(Class type, String methodName, Class[] parameterTypes) throws NoSuchMethodException
    {
        METHOD_LOOP: for (Method method : type.getMethods())
        {
            if (method.getParameterTypes().length != parameterTypes.length)
                continue;
            if (!method.getName().equals(methodName))
                continue;
            Class[] methodParameterTypes = method.getParameterTypes();
            for (int p = 0 ; p < methodParameterTypes.length ; ++p)
            {
                Class methodParameterType = methodParameterTypes[p];
                Class parameterType = parameterTypes[p];
                if (!methodParameterType.isAssignableFrom(parameterType) && !isPrimitive(parameterType, methodParameterType))
                    continue METHOD_LOOP;
            }
            return method;
        }
        throw new NoSuchMethodException(type.getName()+"."+methodName+"("+Arrays.toString(parameterTypes)+")");
    }

    /**
     *
     * @param c1
     * @param c2
     * @return
     */
    public static boolean isPrimitive(Class c1, Class c2)
    {
        return (
            (c1.equals(Integer.class) && c2.equals(int.class))
            || (c1.equals(Short.class) && c2.equals(short.class))
            || (c1.equals(Long.class) && c2.equals(long.class))
            || (c1.equals(Float.class) && c2.equals(float.class))
            || (c1.equals(Double.class) && c2.equals(double.class))
            || (c1.equals(Boolean.class) && c2.equals(boolean.class))
            || (c1.equals(Character.class) && c2.equals(char.class))
        );
    }
}
