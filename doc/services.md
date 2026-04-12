# Services

Services are the core building blocks of your application within Noleme Vault. They represent the objects managed by the DI container.

## Service Declaration

In YAML, services are declared under the `services` key. The most basic declaration requires a `class` name.

```yaml
services:
    my_service:
        class: "com.example.MyService"
```

### Constructor Injection

You can specify constructor arguments using the `constructor` key. Arguments can be literals or references to other services (prefixed with `@`) or variables (enclosed in `##`).

```yaml
services:
    my_database:
        class: "com.example.Database"
        constructor:
            - "jdbc:mysql://localhost:3306/db"
            - "##db_user##"

    my_service:
        class: "com.example.MyService"
        constructor:
            - "@my_database"
```

### Factory Methods

Services can also be instantiated via static factory methods using the `method` key and the `arguments` key.

```yaml
services:
    # Static factory method
    my_service:
        class: "com.example.ServiceFactory"
        method: "createService"
        arguments:
            - "some_config"
```

## Method Invocations

After a service is instantiated, you can call methods on it using the `invocations` key. Each invocation is a list where the first element is the method name, and subsequent elements are the method arguments.

```yaml
services:
    my_service:
        class: "com.example.MyService"
        invocations:
            - ["init", "some_value"]
            - ["setOtherService", "@other_service"]
            - ["start"]
```

## Service Aliasing

You can create an alias for an existing service using the `alias` key.

```yaml
services:
    my_service_alias:
        alias: "my_service"
```

## Service Closing

If a service implements `AutoCloseable`, Noleme Vault can automatically close it when the `Vault` instance is closed. You can explicitly mark a service as closeable using the `closeable` property.

```yaml
services:
    my_closeable_service:
        class: "com.example.MyCloseableService"
        closeable: true
```

When you call `vault.close()`, all services marked as closeable (and those that implement `AutoCloseable` by default in some configurations) will be closed in the reverse order of their registration.
