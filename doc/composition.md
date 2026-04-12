# Composition

Noleme Vault is designed for highly modular configuration using composition. You can split your configuration across multiple files and bring them together.

## Importing Files

You can import other configuration files using the `imports` key at the root of your YAML.

```yaml
imports:
    - "database.yml"
    - "services.json"

services:
    app:
        class: "com.example.App"
        constructor:
            - "@my_database" # Imported from database.yml
```

## Blueprints and Scopes

For more isolation, you can import files into a **scope**. This allows you to treat a configuration file as a reusable "blueprint".

### Defining a Scope

In your main configuration, use the `scopes` key:

```yaml
scopes:
    my_scope: "blueprint.yml"
```

### Accessing Scoped Services

Services from the scoped file are not directly available in the global namespace. To access them, you should bring them into the local namespace using the `use` key.

```yaml
services:
    # Uses the 'remote_service' definition from 'my_scope'
    local_remote_service: { use: remote_service@my_scope }

    local_service:
        class: "com.example.LocalService"
        constructor:
            - "@local_remote_service"
```

## Service Tagging & Aggregation

Tags allow you to group multiple services together. This is extremely useful for things like registering plugins or event listeners.

### Defining a Tag

Before tagging services, you must declare the tag under the root `tags` key.

```yaml
tags:
    app.plugins: {}
```

### Tagging a Service

In the service definition, add one or more tags:

```yaml
services:
    plugin_a:
        class: "com.example.PluginA"
        tags: ["app.plugins"]

    plugin_b:
        class: "com.example.PluginB"
        tags: ["app.plugins"]
```

### Aggregating Tagged Services

You can then inject the collection of tagged services into another service using the `@tag_name` syntax.

```yaml
services:
    plugin_manager:
        class: "com.example.PluginManager"
        constructor:
            - "@app.plugins" # Injects a Collection of services tagged with 'app.plugins'
```

### Advanced Tagging

Tags can also carry metadata, which can be processed by custom modules:

```yaml
services:
    my_service:
        class: "com.example.MyService"
        tags:
            - { id: "my_tag", priority: 10, type: "internal" }
```
