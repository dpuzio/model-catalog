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

### Prerequisites
In order to run the service locally running MongoDB service is required.
Instructions on how to install and run MongoDB can be found here: https://docs.mongodb.com/getting-started/shell/installation/ .

### Environment variables
To run the service locally or in Cloud Foundry, the following environment variables need to be defined:
``VCAP_SERVICES_MODELS_STORE_CREDENTIALS_HOSTNAME`` - a MongoDB server name;
``VCAP_SERVICES_MODELS_STORE_CREDENTIALS_PORT`` - a MongoDB server port;
``VCAP_SERVICES_MODELS_STORE_CREDENTIALS_DBNAME`` - a MongoDB database name;
``VCAP_SERVICES_MODELS_STORE_CREDENTIALS_USERNAME`` - user name used to connect to MongoDB (can be empty);
``VCAP_SERVICES_MODELS_STORE_CREDENTIALS_PASSWORD`` - password used to connect to MongoDB;

### Running
To run the application, use the following command:
```
$ mvn spring-boot:run
```
After starting a local instance, it's available at http://localhost:9913 .
To change the default listening port, please use parameter ``-Dserver.port=[port number]``