# Getting Started with the Verity SDK

This guided walkthrough has three main high-level steps:
1. Getting Connect.Me
2. Launching the Example Application
3. Walking through the Example Application Interactions

There are two paths to launching the example application:
* using `docker` to set up an appropriate environment for this walkthrough
* using local language specific environment for this walkthrough

The `docker` path simplifies the set up process but the local environment walkthrough has a more complete set of instructions for a given language SDK. 

## Getting Connect.Me
1. Download [Connect.Me](https://connect.me/) from the
 [App Store](https://itunes.apple.com/us/app/connect-me/id1260651672?mt=8) or the 
 [Google Play Store](https://play.google.com/store/apps/details?id=me.connect&hl=en).

2. Accept the End User License Agreement.

3. Select **Start Fresh** on the next screen.

4. Select **Use Staging Net**, as shown in the screenshot below.
   
   ![Connect.Me Developer Mode Switch](https://i.postimg.cc/sDj0SzyG/use-staging-net-CM1-4.jpg)

5. Choose an authentication method.

After these steps, Connect.Me is ready for the interactions that are demonstrated by the example application. 

## Launching the Example Application using Docker
The included `Dockerfile` will setup a complete environment for running the example application in all supported languages. This is the quickest way to try the verity-sdk and avoid some complexity around `ngrok` and `libindy` when working on Windows/MacOS.

### Prerequisites
* `docker` is required for this path and installation instructions can be found at [Get Docker Page](https://www.docker.com/get-docker/). 

### Steps:
1. Pull the Verity SDK sample app docker image from the Docker Hub:

   ```sh
   docker pull evernymdev/sdk-sample-app:stable
   ```
   > **NOTE:** Alternatively you can build the `sdk-sample-app` docker image instead of pulling it from the Docker Hub. To build the image run `docker build -f samples/sdk/Dockerfile -t sdk-sample-app .` from the root directory of the `verity-sdk` project.

1. Start a container based on the **sdk-sample-app** docker image. Run the following command:
   ```sh
   docker run -it evernymdev/sdk-sample-app:stable
   ```
   > **NOTE:** The example application will provision an agent, create a key-pair in a local wallet and generate a context json. All of this will happen inside of the docker container. If this docker container is lost, **YOU WILL NOT** be able to continue to use that provisioned agent on the Verity Application.

   > **NOTE:** A couple of process are started when entering the above docker container. There are two ngrok processes, and a simple http server process. In particular, the ngrok processes allows for a public addressable endpoint for the webhook that receives messages from Verity Application. While this is very useful, ngrok does times out after **8 hours**. To reset ngrok, simple re-run the `entrypoint.sh` script that was run when entering the docker container. It is located at `/samples/sdk/entrypoint.sh`. It will kill the existing (and timed out) ngrok processes and start new ones. At which point you are go for another 8 hours.
 
1. Launch example application. Run the following commands in the shell started inside the docker container that was just started:
    * For NodeJs:
        ```sh
        cd /samples/sdk/nodejs-example-app
        node example.js
        ```
    * For Python:
        ```sh
        cd /samples/sdk/python-example-app
        python3 app.py
        ```
    * For Java:
        ```sh
        cd /samples/sdk/java-example-app
        mvn exec:java
        ```
    * For .Net:
        ```sh
        cd /samples/sdk/dotnet-example-app
        dotnet run
        ```

    The example application should start and present the following question:
    ```
    Listening on port 4000
    Provide Provision Token? [y]/n:
    ```

   At this point, the application is running and ready. Proceed to the [Example Application Interactions](../getting-started/getting-started.md#example-application-interactions) section of this document for instructions on walking through the set of interactions executed by the example application.
   
## Launching the Example Application Locally
The example application can be launched from a local development environment. Using this local environment, one can try the verity-sdk in a similar environment that a developer developing with verity-sdk would use. This path requires a few more steps, but these steps would be required to build an application with verity-sdk.

### Prerequisites
* `libindy` -- Install a stable version. Follow the instructions on the 
[indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).
* `Ngrok` -- This is a temporary installation to facilitate early experimentation. Ngrok is used only to serve as a public endpoint for receiving async responses from Verity Application Service. You shouldn't use ngrok for you pilot and production deployments. 
Follow the instructions on the [Ngrok website](https://ngrok.com/download).

### Build
Follow language specific instructions to set up a development environment for the target language verity-sdk. These instructions show how to set up an environment and build the verity-sdk code for each language.

* Build Java example app:
```sh
cd /samples/sdk/java-example-app
mvn compile
```

* Build NodeJS example app:
```sh
cd /samples/sdk/nodejs-example-app
npm install
``` 

* Build Python example app:
```sh
cd /samples/sdk/python-example-app
pip3 install -r requirements.txt
``` 

* Build .NET example app:
```sh
cd /samples/sdk/dotnet-example-app
dotnet build --configuration Release
``` 

### Launch Example Application
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
   
   The `Forwarding` URL is what must be given to the example application during the **Setup** interaction (see interactions below). For the above output, it would be `https://a74616ad.ngrok.io` but every time `ngrok` is started a new URL will be generated, but it will have the following format:
   
   `https://<id>.ngrok.io`
   
   > **NOTE:** `ngrok` must be left running during the whole time the example application is running.
1. **Start application**
    * For NodeJs:
        ```sh
        cd /samples/sdk/nodejs-example-app
        node example.js
        ```
    * For Python:
        ```sh
        cd /samples/sdk/python-example-app
        python3 app.py
        ```
    * For Java:
        ```sh
        cd /samples/sdk/java-example-app
        mvn exec:java
        ```
    * For .NET
        ```sh
        cd /samples/sdk/dotnet-example-app
        dotnet run
        ``` 
    The example application should start and present the following question:
    ```
    Listening on port 4000
    Provide Provision Token? [y]/n:
    ```
   At this point, the application is running and ready. Continue to the next section of this document for instructions on walking through the set of interactions executed by the example application.

## Example Application Interactions
After you have launched the example application in the previous step and have Connect.Me ready to go, you can follow the example application as it completes a series of self-sovereign interactions between the verity-application and Connect.Me.

1. **Setup**
   
   During this setup interaction, three tasks are accomplished:
   
   1. Provisioning an Agent: 
   
      Code Examples: 
        [ [Java](../getting-started/java-verity-flow.md#provisioning-agent-on-verity) ]
        [ [Python](../getting-started/python-verity-flow.md#provisioning-agent-on-verity) ]
        [ [Node](../getting-started/nodejs-verity-flow.md#provisioning-agent-on-verity) ]
        [ [.NET](../getting-started/dotnet-verity-flow.md#provisioning-agent-on-verity) ]
   
      This provisioning creates an Agent that serves one **Identity Owner** During that process keys are generated and exchanged.
      
      During the provisioning interaction, the example application will ask if the user wants to provide a provisioning token. These tokens are required to provision an agent on Evernym's public multi-tenant (SAAS) Verity Application Instances. Evernym will provide these tokens. They are a self-verifying JSON string. If a token is needed, the example will prompt the user for this token JSON string. When passing the token to the example application, it must be a single line string. If needed, the JSON should be minified before being passed to the example application.

       > **NOTE:** Provisioning is a one time action. Once provisioning is successfully completed, the Context object will be created so it can be used for future interactions. The example application will write this context file and read it if it exists. You should backup your context (in the example app it is stored in the file **verity-context.json**) and your libindy wallet (the default wallet location is **$HOME/.indy_client/wallet**) so that you can regain access to your agent on Verity Application instance

   2. Registering a webhook:
   
      Code Examples: 
        [ [Java](../getting-started/java-verity-flow.md#handling-asynchronous-response-messages) ]
        [ [Python](../getting-started/python-verity-flow.md#handling-asynchronous-response-messages) ]
        [ [Node](../getting-started/nodejs-verity-flow.md#handling-asynchronous-response-messages) ]
        [ [.NET](../getting-started/dotnet-verity-flow.md#handling-asynchronous-response-messages) ]
   
       The webhook is a URL for a web server that will receive communication from the Verity server instance. It must be publicly available. (`ngrok` is generally used to meet that requirement.)

   3. Setting up an Issuer identity:
   
      Code Examples: 
        [ [Java](../getting-started/java-verity-flow.md#setting-up-an-issuer-identity) ]
        [ [Python](../getting-started/python-verity-flow.md#setting-up-an-issuer-identity) ]
        [ [Node](../getting-started/nodejs-verity-flow.md#setting-up-an-issuer-identity) ]
        [ [.NET](../getting-started/dotnet-verity-flow.md#setting-up-an-issuer-identity) ]
   
      This is a public identity that will need to be written to a Sovrin Ledger (for this demo use-cases the Sovrin Staging should be used). This identity will be used and associated with Credentials issued by this Agent previously provisioned.
   
   When setting up for the first time, the example application will require these inputs from the user:
   * Provisioning token

   * The URL for the Verity server instance that it will be connecting to
   
   * The `Ngrok` URL for the webhook that you started earlier
   
   Additionally, the issuer identity (DID and Verkey) must be written to the ledger so that the issuer could write schemas and credential definitions to it. The example application will prompt the user if this step should be performed by the application
   ```
   Attempt automated registration via https://selfserve.sovrin.org? [y]/n:
   ```
   or the user wants to manually register Issuer DID and Verkey as a Transaction Endorser for Sovring Staging Net using [Sovrin Self-Serve Website](https://selfserve.sovrin.org/). 

   After initial setup, the context for the Verity SDK is saved to disk and can be reused. 

1. **Connect**

    Code Examples: 
    [ [Java](../getting-started/java-verity-flow.md#connecting) ]
    [ [Python](../getting-started/python-verity-flow.md#connecting) ]
    [ [Node](../getting-started/nodejs-verity-flow.md#connecting) ]
    [ [.NET](../getting-started/dotnet-verity-flow.md#connecting) ]
   
   Connecting is used to form a pairwise relationship between the Identity owner represented by the Agent provision, and the user of Connect.Me. This connection interaction is between two parties and happens over asynchronous messages between these parties. 
   
   Connecting is largely automated in the example application. You can view incoming messages 
   in the console as they arrive from the Verity server instance.
    
   A QR code image will be generated and placed in the current working directory. Scan this QR code with Connect.Me to finish forming the connection between the Verity server instance and Connect.Me.

1. **Write Schema to Ledger**

    Code Examples: 
    [ [Java](../getting-started/java-verity-flow.md#write-schema-to-ledger) ]
    [ [Python](../getting-started/python-verity-flow.md#write-schema-to-ledger) ]
    [ [Node](../getting-started/nodejs-verity-flow.md#write-schema-to-ledger) ]
    [ [.NET](../getting-started/dotnet-verity-flow.md#write-schema-to-ledger) ]
   
   The `schema` is an essential element of Verifiable Credentials Exchange. This `schema` must be publicly available from a trusted source. The Identity ledger provided by Sovrin is used for the purpose. The `schema` is used to express the shape of the data in the credential. This interaction uses the `write-schema` protocol to both create and write the `schema` object to the ledger.

   Writing the schema to the ledger is fully automated in the example application. You can view 
   incoming messages in the console as they arrive from the Verity server instance.

1. **Write Credential Definition to Ledger**

    Code Examples: 
    [ [Java](../getting-started/java-verity-flow.md#write-credential-definition-to-ledger) ]
    [ [Python](../getting-started/python-verity-flow.md#write-credential-definition-to-ledger) ]
    [ [Node](../getting-started/nodejs-verity-flow.md#write-credential-definition-to-ledger) ]
    [ [.NET](../getting-started/dotnet-verity-flow.md#write-credential-definition-to-ledger) ]

   The `credDef` object is similar to the `schema` object. It is also used for Verifiable Credentials Exchange and writen to an identity ledger. The `credDef` is used to hold public keys for an Issuer. This interaction uses the `write-cred-def` protocol to both create and write the `credDef` object to the ledger.

   Writing the credential definition to the ledger is fully automated in the example application. 
   You can view incoming messages in the console as they arrive from the Verity server instance.

1. **Issue Degree Credential**

    Code Examples: 
    [ [Java](../getting-started/java-verity-flow.md#issue-credential) ]
    [ [Python](../getting-started/python-verity-flow.md#issue-credential) ]
    [ [Node](../getting-started/nodejs-verity-flow.md#issue-credential) ]
    [ [.NET](../getting-started/dotnet-verity-flow.md#issue-credential) ]
   
   The `issue-credential` protocol used in this interaction is the heart of the Verifiable Credentials Exchange. Using the `schema`, `credDef` and `connection` created before, a Credential can be issued. 
   
   Issuing a credential is automated in the example application. It will wait for steps to be 
   completed in Connect.Me asynchronously. Accept the credential in Connect.Me when it pops up.

1. **Request Proof Presentation**

    Code Examples: 
    [ [Java](../getting-started/java-verity-flow.md#request-proof-presentation) ]
    [ [Python](../getting-started/python-verity-flow.md#request-proof-presentation) ]
    [ [Node](../getting-started/nodejs-verity-flow.md#request-proof-presentation) ]
    [ [.NET](../getting-started/dotnet-verity-flow.md#request-proof-presentation) ]
   
   The `present-proof` protocol used in this interaction allow the final step of the Verifiable Credentials Exchange. Requesting a presentation of proof derived by a verifiable credential allow for the exchange of information from parties that test each other. 

   The request for proof is automated in the example application. It will wait for steps to be 
   completed in Connect.Me asynchronously. Accept the proof in Connect.Me when it pops up.
   
After the proof is exchanged with the Verity server instance, the simulated interaction is complete, and the example application terminates.

## Additional Notes:
> **NOTE:** The Verity-SDK holds and manages one key-pair in the libindy wallet which is used to communicate with the Verity Application Service. The public verification key of the key-pair is part of the Context object. It can be viewed using the `sdkVerKey` method on the Context object or the `sdkVerKey` field when the Context object is converted to JSON.

> **NOTE:** A Verity-SDK Context can also be used with the Verity REST API. Use the `restApiToken` method on the Context object to get the required API key. 

