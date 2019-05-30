# Java SDK for Verity

## Overview

This directory contains both the Java SDK and an example application that consumes the SDK.  The JAR file is available at https://evernym.mycloudrepo.io. See [below](#maven) for information on consuming this artifact.

## Reference Documentation

To generate the Javadoc reference documentation:

```sh
cd sdk/java-sdk
mvn site
```

then open sdk/java-sdk/target/site/apidocs/index.html

## Steps to Run the Example Application (Ubuntu 16.04)

1. Install libindy, Java 8 (JDK) and Maven

	```sh
	sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88
	sudo add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial master" # Add Sovrin apt repository
	sudo apt-get update
	sudo apt-get install libindy default-jdk maven
	```

2. Clone this repository

	```sh
	git clone git@gitlab.corp.evernym.com:dev/verity/verity-sdk.git
	cd verity-sdk
	```

3. Build the Java SDK for Verity

	```sh
	cd sdk/java-sdk
	mvn package
	```

4. Start the Mock Verity service

	```sh
	docker login gitlab.corp.evernym.com:4567
	docker run -it --network host gitlab.corp.evernym.com:4567/dev/verity/verity-sdk/verity-server-image
	```

5. Provision against Mock Verity

	```sh
	# From the root of the repository
	cd tools
	python3 -m pip install -r requirements.txt --user
	python3 provision_sdk.py --wallet-name java-sdk-test-wallet http://localhost:8080 <wallet key>
	# copy the resulting config to sdk/java-sdk/example/verityConfig.json
	```

6. Run the example.

	```
	cd sdk/java-sdk/example
	mvn package
	./run.sh
	```

NOTE: If you restart Mock Verity at any point, you will need to provision again.

<a id="maven"></a>
## Adding the Java SDK for Verity to your Maven Application

In order for your Maven Client to authenticate with CloudRepo, you’ll need to add your credentials to a <server> entry to the settings.xml file, usually located in the `$HOME/.m2` directory.

Authentication is required to read and write to/from private repository URLs.

A template settings.xml for you to use is below:

Note: Replace the provided username and password with the placeholders.

```xml
<settings>
  <servers>
    <server>
      <!-- id is a unique identifier for a single repository.-->
      <id>io.cloudrepo</id>
      <!-- Email Address of the Repository User (Account Owner Credentials WILL NOT WORK). -->
      <username>repository-user-email-address</username>
      <!-- Password of the Repository User. -->
      <password>repository-user-password</password>
    </server>
  </servers>
</settings>
```

### pom.xml Overview

All Maven projects use a Project Object Model (POM) file to specify the location of the repositories to read and write artifacts to/from.

Configuration of the POM file for downloading artifacts is distinct from publishing artifacts. 

### Retrieving Artifacts from CloudRepo (Authenticated Access)

Repositories specified in the <repositories> block define the repositories that Maven will use to retrieve artifacts.

The following example shows how to retrieve artifacts via authenticated (private) access.

Add the following to your pom.xml file:

```xml
<project>
  <!-- ...  -->
  <!-- Other Project Configuration (dependencies, etc.)-->
  <!-- ...  -->
  <repositories>
    <repository>
      <!--
      The username and password are retrieved by looking for the Repository
      Id in the $HOME/.m2/settings.xml file.
      -->
      <!-- id Must Match the Unique Identifier in settings.xml -->
      <id>io.cloudrepo</id>
      <url>https://evernym.mycloudrepo.io/repositories/verity-sdk-java</url>
      <releases/>
    </repository>
  </repositories>
</project>
```

After the repository has been configured, Maven will retrieve your artifacts stored in CloudRepo as part of its build process.

One way to trigger a maven build is to execute the following command in the same directory as your POM file:

mvn install

### Retrieving Artifacts from CloudRepo (Public Access)

**_Public read access for this repository is NOT yet enabled, but this is included as reference for when it is._**

Unauthenticated (public) access must use a different URL than the one that is used for private access.

The following example shows how to retrieve artifacts via anonymous (public) access.

Add the following to your pom.xml file:

```xml
<project>
  <!-- ...  -->
  <!-- Other Project Configuration (dependencies, etc.)-->
  <!-- ...  -->
  <repositories>
    <repository>
      <!--
      No id is required as authentication doesn't happen for public repositories.
      -->
      <id>io.cloudrepo</id>
      <url>https://evernym.mycloudrepo.io/public/repositories/verity-sdk-java</url>
      <releases/>
    </repository>
  </repositories>
</project>
```
