# Noleme Vault

[![Maven Build](https://github.com/noleme/noleme-vault/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/noleme/noleme-vault/actions/workflows/maven.yml)
[![Maven Central Repository](https://maven-badges.herokuapp.com/maven-central/com.noleme/noleme-vault/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.noleme/noleme-vault)
[![javadoc](https://javadoc.io/badge2/com.noleme/noleme-vault/javadoc.svg)](https://javadoc.io/doc/com.noleme/noleme-vault)
![GitHub](https://img.shields.io/github/license/noleme/noleme-vault)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fnoleme%2Fnoleme-vault.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fnoleme%2Fnoleme-vault?ref=badge_shield)

A library providing DI with JSR-330 annotations and extensible YML/JSON configuration.

The intended goal is to have both "traditional" DI capabilities and a non-intrusive, runtime-evaluated configuration system with an emphasis on composition.
Each configuration file represents a small graph of objects that can be composed with others in order to create an application.

Implementations found in this package shouldn't be tied to any specific Noleme project.

_Note: This library is considered as "in beta" and as such significant API changes may occur without prior warning._

## I. Installation

Add the following in your `pom.xml`:

```xml
<dependency>
    <groupId>com.noleme</groupId>
    <artifactId>noleme-vault</artifactId>
    <version>0.16</version>
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
    private final MyClass service;
    private final MyOtherClass otherService;

    @Inject
    public MyService(MyClass service, @Named("my_other_service") MyOtherClass otherService)
    {
        this.service = service;
        this.otherService = otherService;
    }
}
```

..and do the following:

```java
var vault = Vault.with("my_conf.yml");
MyService service = vault.instance(MyService.class);
```

It's also possible to use field annotations and proceed the following way:

```java
public class MyService
{
    @Inject private MyClass service;
    @Inject private MyOtherClass otherService;
}
```

```java
MyService service = vault.inject(new MyService());
```

Alternatively we could directly query one of the declared services:

```java
MyClass myService = vault.instance(MyClass.class, "my_service");
```

Other features that will need to be documented include:

* import of dependency json/yml files
* service method invocation
* service instantiation via static method call
* service aliasing
* service closing
* service container composition
* service tagging & aggregation
* service blueprints (scoped imports)
* custom and generic modules
* custom preprocessing routines

_TODO_

## IV. Dev Installation

This project will require you to have the following:

* Java 11+
* Git (versioning)
* Maven (dependency resolving, publishing and packaging) 


## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fnoleme%2Fnoleme-vault.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fnoleme%2Fnoleme-vault?ref=badge_large)
