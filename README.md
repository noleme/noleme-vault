# Lumio Vault

[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/lumio-medical/lumio-vault/Java%20CI%20with%20Maven)](https://github.com/lumio-medical/lumio-vault/actions?query=workflow%3A%22Java+CI+with+Maven%22)
[![Maven Central Repository](https://maven-badges.herokuapp.com/maven-central/com.lumiomedical/lumio-vault/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.lumiomedical/lumio-vault)
[![javadoc](https://javadoc.io/badge2/com.lumiomedical/lumio-vault/javadoc.svg)](https://javadoc.io/doc/com.lumiomedical/lumio-vault)
![GitHub](https://img.shields.io/github/license/lumio-medical/lumio-vault)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Flumio-medical%2Flumio-vault.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Flumio-medical%2Flumio-vault?ref=badge_shield)

A library providing DI with JSR-330 annotations and extensible YML/JSON configuration.

Implementations found in this package shouldn't be tied to any specific Lumio project.

_Note: This library is considered as "in beta" and as such significant API changes may occur without prior warning._

## I. Installation

Add the following in your `pom.xml`:

```xml
<dependency>
    <groupId>com.lumiomedical</groupId>
    <artifactId>lumio-vault</artifactId>
    <version>0.10</version>
</dependency>
```

## II. Notes on Structure and Design

_TODO_

## III. Usage

A basic example of using this library with a `yml` configuration file:

Given a dummy configuration file `my_conf.yml`:

```yaml
variables:
    my_var: 12.34
    my_other_var: "interesting"
    my_env_var: ${MY_VAR}

services:
    my_service:
        class: "me.company.MyClass"
        constructor:
            - "not so interesting"
            - "##my_var##"

    my_other_service:
        class: "me.company.MyOtherClass"
        constructor:
            - "##my_other_var##"
``` 

We could perform injection via annotations on a dummy class such as:

```java
public class MyService
{
    @Inject @Named("my_service")
    public MyClass service;
    @Inject @Named("my_other_service")
    public MyOtherClass otherService;
}
```

..and do the following:

```java
MyService service = Vault.with("my_conf.yml").inject(new MyService());
```

..one of the neat things we can do, is programmatically override parts of the configuration:

```java
MyService service = Vault.with("my_conf.yml", defs -> {
    defs.setVariable("my_var", 34.56); //my_var will now equal 34.56 upon injection
}).inject(new MyService());
```

Alternatively we could directly query one of the declared services:

```java
MyClass myService = Vault.with("my_conf.yml").instance(Key.of(MyClass.class, "my_service"));
```

Other features that will need to be documented include:

* import of dependency json/yml files
* service method invocation
* service instantiation via static method call
* service aliasing
* service closing
* service container composition
* custom modules
* custom preprocessing routines

_TODO_

## IV. Dev Installation

This project will require you to have the following:

* Java 11+
* Git (versioning)
* Maven (dependency resolving, publishing and packaging) 


## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Flumio-medical%2Flumio-vault.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Flumio-medical%2Flumio-vault?ref=badge_large)