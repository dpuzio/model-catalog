[![Dependency Status](https://www.versioneye.com/user/projects/57236d4cba37ce00350af79b/badge.svg?style=flat)](https://www.versioneye.com/user/projects/57236d4cba37ce00350af79b)

# model-catalog
Service for exposing data models

## Run

### Build project
To build application you can use maven:

```mvn clean package```

It will generate two jar files in target directory:

```
model-catalog-<version>.jar
model-catalog-<version>-api.jar
```

The first one is model-catalog application. The other one is a model provider api. All models providers should implement it.