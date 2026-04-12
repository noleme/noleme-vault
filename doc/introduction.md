# Introduction

Noleme Vault is a dependency injection library for Java that combines traditional DI capabilities (via JSR-330 annotations) with an extensible, runtime-evaluated configuration system based on YAML or JSON.

The library is designed with an emphasis on **composition**, allowing you to define small graphs of objects in configuration files and compose them to build complex applications.

## Design Philosophy

The core of Noleme Vault is built around a three-stage lifecycle:

1. **Parsing & Extraction**: The `VaultParser` reads configuration files and extracts `Definitions`. These definitions act as a blueprint for your dependency graph.
2. **Compilation & Registration**: The `VaultFactory` processes these `Definitions` to resolve dependencies, validate the graph structure, and register services into a `Cellar`.
3. **Runtime & Injection**: the `Vault` instance then can be used as the injection service, leveraging the `Cellar` and providing DI capabilities via JSR-330 annotations or direct queries.

## Key Components

* **Vault**: The main entry point for the library. It provides a high-level API for instantiating services and performing injections.
* **Cellar**: A container for runtime objects. It holds the actual instances of services and the values of configuration variables.
* **Definitions**: A registry of service and variable metadata before they are instantiated.
* **VaultParser**: An extensible component responsible for translating external configuration formats (eg. YML/JSON) into `Definitions`.
* **VaultFactory**: The engine that transforms `Definitions` into a live `Cellar` of services.
