[![Dependency Status](https://www.versioneye.com/user/projects/57236d4cba37ce00350af79b/badge.svg?style=flat)](https://www.versioneye.com/user/projects/57236d4cba37ce00350af79b)

# model-catalog
Service for exposing data models

## How to build
It's a Spring Boot application build by maven. All that's needed is a single command to compile, run tests and build a jars:
```
$ mvn verify
```
It will generate two jar files in `target` directory:
```
model-catalog-<version>.jar
model-catalog-<version>-api.jar
```
The first one is model-catalog application. The other one is a model provider api. All models providers should implement it.

## How to run locally

```
$ mvn spring-boot:run
```
After starting a local instance, it's available at http://localhost:9999
