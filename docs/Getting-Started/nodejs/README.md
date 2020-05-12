# Getting Started with the Verity SDK for NodeJs

### Previous Step

[Stand Up an Evaluation Verity Application](../../../README.md#cloud)

---

This procedure shows you how to set up your NodeJs development environment to use the Verity SDK. As desired, 
run the example application to see a demonstration of how the interactions take place. 

**Prerequisites**

Install the following items:
* `libindy` -- Install a stable version. Follow the instructions on the 
[indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).
* `Ngrok` -- This is a temporary installation to facilitate early experimentation. 
Follow the instructions on the [Ngrok website](https://ngrok.com/download).
* `NodeJs` -- Follow the instructions on the [NodeJs website](https://nodejs.org/en/).

> **NOTE:** Verify that you have read access to the Public NPM repo.

## Build the NodeJs Verity SDK
Go to the directory where you extracted the tarball.

1. Go to the NodeJs Verity SDK project folder:
  
   ```sh
   cd verity-sdk/sdk/nodejs-sdk
   ```

2. Install dependencies:

   ```sh
   npm install
   ```
   
## Run the Application
1. **Start `ngrok`**

   The Verity server instance is not running in the same local environment as the example application. The Verity server instance communicates with the SDK via a webhook. This webhook must be accessible by the Verity server instance, but the example application can only start a local webhook endpoint. `ngrok` is used to provide a publicly available endpoint that tunnels to the local webhook endpoint that is started by the Example App. 
   
   See [Ngrok's product page](https://ngrok.com/product) for a more detailed description of what Ngrok is and what it does. 
   
   The example application uses port `4000` by default for the webhook endpoint. So, `ngrok` much be launched pointing to `4000`.
   
   Starting `ngrok` command. 
   ```sh
   ngrok http 4000
   ```
   
   After starting, `ngrok` should provide output that looks like this:
   ```sh
   ngrok by @inconshreveable
                                                                             
   Session Status                online
   Session Expires               6 hours, 11 minutes
   Version                       2.3.35
   Region                        United States (us)
   Web Interface                 http://127.0.0.1:4040
   Forwarding                    http://a74616ad.ngrok.io -> http://localhost:9003
   Forwarding                    https://a74616ad.ngrok.io -> http://localhost:9003 
   ```
   
   The `Forwarding` URL is what must be given to the example application during the **Setup** interaction (see interactions below). For the above output, it would be `https://a74616ad.ngrok.io` but ever time `ngrok` is started a new URL will be generated, but it will have the following format:
   
   `https://<id>.ngrok.io`
   
   > **NOTE:** `ngrok` must be left running during the whole time that the example application is running.

<a id="connectme"></a>

2. **Set up Connect.Me**

   The interaction that is simulated in the example application requires the Connect.Me mobile app. 

   * Find Connect.Me in the Apple and Android app stores. 

   * Follow the [Connect.Me Setup Instructions](../ConnectMe.md)
   
3. **Launch the example application**
   
   Start the example application and begin the simulated interaction:
   
   ```sh
   node example.js
   ``` 

---
### Next Step

Continue to [Interactions](../Interactions.md).

---
> **NOTE:** A Verity-SDK Context can also be used with the Verity REST API. Use the `restApiToken` method on the Context object to get the required API key. 
>
>See the Open API docs for the Verity REST API at https://app.swaggerhub.com/apis/evernym/verity-rest-api/0.6 


 
© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.