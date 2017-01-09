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

Model-catalog by default stores artifact files on local disk in `/data/artifacts` path - the path can be changed by setting environment variable `STORAGE_LOCAL_BASEPATH`.

### Environment variables
To run the service locally the following environment variables need to be defined:

* `SSO_TOKENKEY` - URL to the OAuth token key service (e.g. `http://uaa.<env_domain>/token_key`);
* `STORAGE_LOCAL_BASEPATH` - a path on the local disk for storing artifact files (e.g. `/tmp/artifacts`) (OPTIONAL); 

### Running
To run the application, use the following command:
```
$ mvn spring-boot:run
```
After starting a local instance, it's available at http://localhost:9913 .
To change the default listening port, please use parameter ``-Dserver.port=[port number]``

### Testing
Application can be tested using `curl` tool. Few examples:

```
curl -H "Authorization: $TOKEN" http://<model-catalog-host>:<model-catalog-port>/api/v1/models?orgId=defaultorg

curl -X POST -H "Authorization: $TOKEN" -H "Content-type: application/json" -d '{"name": "sample-model1","revision":"1.0","algorithm":"GBM","creationTool":"manual","description":"Sample Model One","artifactsIds":[]}' http://<model-catalog-host>:<model-catalog-port>/api/v1/models?orgId=defaultorg

curl -v -X POST -H "Authorization: $TOKEN" -F 'artifactActions=["PUBLISH_TAP_SCORING_ENGINE"];type=application/json' -F "artifactFile=@test.txt;type=application/octet-stream" http://<model-catalog-host>:<model-catalog-port>/api/v1/models/<model-id>/artifacts

```

`TOKEN` can be obtained from the TAP CLI tool. For example:
```
tap login http://api.<env_domain> <user> <password> -v DEBUG

export TOKEN="bearer <access_token_pasted_from_tap_cli>"
```
