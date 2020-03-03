# Getting Started with the Verity SDK for Java

### Previous Step

[Stand Up an Evaluation Verity Application](../../../README.md#cloud)

The Java integration code example is a simple showcase of the Java Verity SDK.

**Prerequisites**

Install the following items:
* `libindy` - Follow the instructions on the [indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).
* `Ngrok` - Follow the instructions on the [Ngrok website](https://ngrok.com/download).
* `Maven` - Follow the instructions on the [Maven website](http://maven.apache.org/download.cgi)

## Clone the Verity SDK Repo 
<!--This step is contingent on how the repo is delivered-->

```sh
git clone git@gitlab.corp.evernym.com:dev/verity/verity-sdk.git
```

**Repo Requirements**

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

## Build the Integration Code Example

1. Go to the integration code example project folder:
  
   ```sh
   cd verity-sdk/sdk/java-sdk/example
   ```

2. Compile the integration code example:

   ```sh
   mvn compile
   ```
   
## Run the Application

1. **Start `Ngrok`**

   `Ngrok` allows the customer integration code to reach a webhook endpoint that the integration code example starts. By default, the integration code example uses port `4000`, so `Ngrok` must be started to proxy to that local port:
   
   ```sh
   ngrok http 4000
   ```
   
   `Ngrok` should be running at the same time as the integration code example, and the endpoint should be given to the example during the *Setup* interaction.

   <a id="connectme"></a>

1. **Set up Connect.Me**

   The interaction that is simulated in the integration code example requires the Connect.Me mobile app. 

   * Find Connect.Me in the Apple and Android app stores. 

   * Follow the [Connect.Me Setup Instructions](../ConnectMe.md)

   
1. **Launch the integration code example**

   Start the integration code example and begin the simulated interaction:
   
   ```sh
   mvn exec:java
   ``` 
  
### Next Step

Continue to [Interactions](../Interactions.md).

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.