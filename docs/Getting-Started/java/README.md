# Getting Started with the Verity SDK for Java

The Java example application is a simple showcase of the Java Verity SDK.

Before performing this procedure go to: [Standing Up an Evaluation Verity Application](../VerityInstance.md)

**Prerequisites**

Install the following items:
* `libindy` - Follow the instructions on the [indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).
* `Ngrok` - Follow the instructions on the [Ngrok website](https://ngrok.com/download).
* `Maven` - Follow the instructions on the [Maven website](http://maven.apache.org/download.cgi)

## Clone the Verity SDK Repo

```sh
git clone git@gitlab.corp.evernym.com:dev/verity/verity-sdk.git
```

**Repo Requirements**
<!--What do they need to do about these? Get access? Download them?-->
* Public Sovrin Maven repo
* Public Maven Central repo

## Build the Java Verity SDK

1. Go to the Java Verity SDK project folder
  
   ```sh
   cd verity-sdk/sdk/java-sdk
   ```

2. Compile, package, and install the Java Verity SDK in the local Maven repo (see ~/.m2) <!--Is this a reference to somewhere? A link?-->:

   ```sh
   mvn install
   ```

## Build the Example Application

1. Go to the example application project folder:
  
   ```sh
   cd verity-sdk/sdk/java-sdk/example
   ```

2. Compile the example application:

   ```sh
   mvn compile
   ```
   
## Run the Application

1. **Start `Ngrok`**

   `Ngrok` allows the Verity application to reach a webhook endpoint that the example application starts. By default, the example application uses port `4000`, so `Ngrok` must be started to proxy to that local port:
   
   ```sh
   ngrok http 4000
   ```
   
   `Ngrok` should be running at the same time as the example application, and the endpoint should be given to the example during the *Setup* interaction.
   
1. **Set up Connect.Me**

   The interaction that is simulated in the example application requires the Connect.Me mobile app. 

   * Find Connect.Me in the Apple and Android app stores. 
   * Follow the [Connect.Me Setup Instructions](../ConnectMe.md)

   
1. **Launch the example application**

   Start the example application and begin the simulated interaction:
   
   ```sh
   mvn exec:java
   ``` 
  
Continue to [Interactions](../Interactions.md).

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.