# Variables

Variables allow you to externalize configuration values and reuse them across your service definitions.

## Variable Declaration

Variables are declared under the `variables` key in your configuration file.

```yaml
variables:
    database.host: "localhost"
    database.port: 3306
    app.name: "My Application"
```

## Using Variables

You can use variables in your service definitions by enclosing their name in double hash signs (`##`).

```yaml
services:
    my_database:
        class: "com.example.Database"
        constructor:
            - "##database.host##"
            - "##database.port##"
```

## Environment Variables

Noleme Vault supports the injection of environment variables using the `${NAME:default}` syntax.

```yaml
variables:
    db_password: "${DB_PASSWORD:admin}"
```

In this example, if the environment variable `DB_PASSWORD` is set, its value will be used; otherwise, the default value `admin` will be applied.

## List Variables

Variables can also be defined as lists. These can contain literals, other variable references, or environment variables.

```yaml
variables:
    my_variable: 'abcde'
    my_list:
        - 'something'
        - 2345
        - 12.34
        - false
        - '##my_variable##'
        - ${MY_VAR:default_value}
    
    my_inline_list: [1, 2, 3]
```

## Dynamic Overrides

Variables can be dynamically overridden or completed during the parsing phase using `VaultAdjuster`. This is useful for programmatically injecting configuration at runtime.

```java
var vault = Vault.with("config.yml", VaultAdjuster.variables(vars -> {
    vars.set("database.host", "prod-db-host");
    vars.set("database.port", 5432);
}));
```
