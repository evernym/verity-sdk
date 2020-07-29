# Java Verity SDK

This is the Java SDK for Evernym's Verity application. 

Instructions for setting up your Java development environment to use the Verity SDK.

## Prerequisites
Install the following items:
* `libindy` -- Install a stable version. Follow the instructions on the 
[indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).

> **NOTE:** Verify that you have read access to the Public Sovrin Maven repo.

## Get Latest Java Verity SDK
See [mvnrepository artifact](https://mvnrepository.com/artifact/com.evernym.verity/verity-sdk) page to find the latest release. 

Add verity-sdk as a dependency to your desired build tool.

Example: `Maven`
```xml
<dependency>
  <groupId>com.evernym.verity</groupId>
  <artifactId>verity-sdk</artifactId>
  <version>0.4.0</version>
</dependency>
```

## Develop the Java Verity SDK

### Development Prerequisites
* `Maven` -- Follow the instructions on the [Maven website](http://maven.apache.org/download.cgi)

### Build
```sh
mvn install
```


### Hack away