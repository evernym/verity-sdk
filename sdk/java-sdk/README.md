# Java Verity SDK

This is the Java SDK for Evernym's Verity application. 

Instructions for setting up your Java development environment to use the Verity SDK.

## Prerequisites
Install the following items:
* `libindy` -- Install a stable version. Follow the instructions on the 
[indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).
* `Maven` -- Follow the instructions on the [Maven website](http://maven.apache.org/download.cgi)

> **NOTE:** Verify that you have read access to the Public Sovrin Maven repo and the Public Maven Central repo.
>
## Build the Java Verity SDK

Go to the directory where you extracted the tarball.

1. Go to the Java Verity SDK project folder:

   ```sh
   cd verity-sdk/sdk/java-sdk
   ```

2. Compile, package, and install the Java Verity SDK in the local Maven repo (see ~/.m2):

   ```sh
   mvn install
   ```

## Build the example application

1. Go to the example application project folder:
  
   ```sh
   cd verity-sdk/sdk/java-sdk/example
   ```

2. Compile the example application:

   ```sh
   mvn compile
   ```