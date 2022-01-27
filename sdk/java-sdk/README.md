# Java Verity SDK

This is the Java SDK for Evernym's Verity application. 

Instructions for setting up your Java development environment to use the Verity SDK.

## Prerequisites
Install the following items:
* `libvdrtools` -- Installation instructions can be found [here](https://gitlab.com/evernym/verity/vdr-tools#installing).

> **NOTE:** Verify that you have read access to the Public Sovrin Maven repo.

## Get Latest Java Verity SDK
See [maven artifact](https://search.maven.org/artifact/com.evernym.verity/verity-sdk) page to find the latest release. 

Add verity-sdk as a dependency to your desired build tool.

Example: `Maven`
```xml
<dependency>
  <groupId>com.evernym.verity</groupId>
  <artifactId>verity-sdk</artifactId>
  <version>0.4.0</version>
</dependency>
```
## Documentation
**API Documentation (JavaDoc)**:
* [0.5.0](https://developer.evernym.com/doc/java/0.4.0/index.html)

## Develop the Java Verity SDK

### Development Prerequisites
* `Maven` -- Follow the instructions on the [Maven website](http://maven.apache.org/download.cgi)

### Build
```sh
mvn install
```


### Hack away