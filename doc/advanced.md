# Advanced Features

Noleme Vault is highly extensible, allowing you to hook into the parsing and compilation process.

## JSR-330 and Programmatic DI

Noleme Vault supports a comprehensive set of JSR-330 annotations (`@Inject`, `@Named`, `@Singleton`) for constructor and field injection, as well as a programmatic module system using `@Provides`.

Detailed information can be found in the [JSR-330 Support](jsr330.md) chapter.

## Custom Parsing Modules

You can extend the configuration format by registering custom `VaultModule` implementations. Each module is responsible for processing a specific root key in your YAML/JSON files.

### Implementing a Module

```java
public class MyCustomModule implements VaultModule {
    @Override
    public String identifier() {
        return "my_custom_key";
    }

    @Override
    public void process(ObjectNode node, Definitions definitions) throws VaultParserException {
        // node contains the JSON/YAML content under 'my_custom_key'
        // you can register new services or variables into 'definitions'
    }
}
```

### Registering the Module

```java
var parser = new VaultCompositeParser();
parser.register(new MyCustomModule());

var vault = Vault.builder()
    .with("config.yml")
    .setFactory(new VaultFactory(parser))
    .build();
```

## Preprocessors

Preprocessors allow you to modify the entire configuration tree before it's passed to the modules. This is useful for global transformations.

```java
public class MyPreprocessor implements VaultPreprocessor {
    @Override
    public ObjectNode preprocess(ObjectNode rootNode) throws VaultParserException {
        // Perform global modifications to the root configuration node
        return rootNode;
    }
}
```

## Programmatic Container Composition

In addition to YAML-based imports, you can compose containers programmatically using `VaultBuilder`. This allows you to combine existing `Cellar` or `Definitions` instances.

```java
var vault = Vault.builder()
    .with("base_config.yml")
    .with(new MyCustomModule())
    .with(existingCellar)
    .build();
```
