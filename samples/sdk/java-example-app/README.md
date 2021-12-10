### Java Example Application
An example application that demonstrate simple SSI use-cases using the Java Verity SDK. 

See [Getting Started](../../../docs/getting-started/getting-started.md) guide for a guided tutorial that makes use of this example application.  

### Prerequisites
Install the following items:
* `libvdrtools` -- Installation instructions can be found [here](https://gitlab.com/evernym/verity/vdr-tools#installing).
* `Ngrok` -- This is a temporary installation to facilitate early experimentation. 
* `Maven` -- Follow the instructions on the [Maven website](http://maven.apache.org/download.cgi)

> **NOTE:** Verify that you have read access to the Public Sovrin Maven repo.

### Build the example application
```sh
mvn compile
```

### Run example application
```sh
mvn exec:java
```