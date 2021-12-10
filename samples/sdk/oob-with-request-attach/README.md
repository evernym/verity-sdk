# Sample Out-of-Band request-attach app 

This application demonstrates request-attach functionality in the Out-of-Band (OOB) protocol.
For more information on the Out-of-Band protocol check [Aries RFC 0434](https://github.com/hyperledger/aries-rfcs/blob/master/features/0434-outofband/README.md)

This application is using Verity NodeJS SDK.

When issuing credential a credential offer can be attached to the OOB invitation.
When requesting proof a proof request can be attached to the OOB invitation.
This allows for supression of connection establishment interactions required from the Holder and a smoother UX experience.

> NOTE: When there is a credential offer or a proof request attached to the invitation, the ConnectMe app will implicitly accept the connection (connection acceptance screen will not be shown in the UI) and will present credential offer or proof request from the OOB invitation attachment.

A new argument `byInvitation` has been introduced in the  **PresentProof** and **IssueCredential** protocol constructors.
Default value for this argument is **false**, which requires that connection has already been established prior to issuing credential or requesting proof.

However, when this argument is set to **true**, then connection with the Issuer/Verifier can be established during **IssueCredential** or **PresentProof** protocol.

After sending a credential offer (or requesting a proof) with the `byInvitation` flag set to **true**, Verity Application Service will respond with the `protocol-invitation` signal message, in which `inviteURL` field will contain OOB invitation with credential offer or proof request attached.
Since `inviteURL` with attachments can get very long, the `protocol-invitation` message will also contain the `shortInviteURL` field with a shortened URL which redirects to the `inviteURL` and which is more friendly for encoding into QR code.

> NOTE: Improved UX comes at the expense of privacy. In the case of credential offer, the **inviteURL** will contain base58 encoded values of credential attributes (PII data). In the case of proof request, the **inviteURL** will contain requested attributes names in the QR code (no PII is present in the QR code in this case)

## Launching application using Docker

Prepared `Dockerfile` will setup a complete environment for running this sample application. This is the quickest way to try the application and avoid some complexity around `ngrok` and `libvdrtools` when working on Windows/MacOS.

### Prerequisites
* `docker` is required for this path and installation instructions can be found at [Get Docker Page](https://www.docker.com/get-docker/). 

### Steps:
1. Pull the Verity SDK sample app docker image from the Docker Hub:

   ```sh
   docker pull evernymdev/sdk-sample-app:stable
   ```
   > **NOTE:** Alternatively you can build the `sdk-sample-app` docker image instead of pulling it from the Docker Hub. To build the image run `docker build -f samples/sdk/Dockerfile -t sdk-sample-app .` from the root directory of the `verity-sdk` project.
2. Start a container based on the **sdk-sample-app** docker image. Run the following command:
   ```sh
   docker run -p 4000:4000 -it evernymdev/sdk-sample-app:stable
   ```
   > **NOTE:** The container will start the Ngrok process in the docker entrypoint. The ngrok processes allows for a public addressable endpoint for the webhook that receives messages from Verity Application. While this is very useful, ngrok does times out after **8 hours**. To reset ngrok, simple re-run the `entrypoint.sh` script that was run when entering the docker container. It is located at `/scripts/entrypoint.sh`. It will kill the existing (and timed out) ngrok processes and start new ones. At which point you are go for another 8 hours.
 
    Launch the application. Run following commands in the started docker container:
    ```sh
    cd /samples/sdk/oob-with-request-attach
    node oob-with-request-attach.js
    ```

    When the application is started for the first time it will provision one Issuer agent and one Verifier agent and will update their webhook endpoints to use the created Ngrok tunnel. It will also perform all necessary steps required for credential issuance (setup Issuer keys, register Issuer DID/Verkey on the ledger, create schema and credential definition). 
    During first-time setup, the application will prompt user for some inputs (e.g. provide provisioning token, answer y/n to some questions)
    The application will persist issuer context, verifier context and credential definition in the files inside the application folder.

    If the application is re-run it will reuse contexts persisted in the local files instead of provisioning from scratch.

    The application will print all messages received from VAS on the console output of the docker container.

    When the line
    ```
    Listening on port 4000
    ```
    is printed on the console output, the application is provisioned and ready. 
    
    Visit the address http://localhost:4000 from the browser of your host machine to try out the app.
   

## Launching the application locally

The application can be launched from a local development environment. Using this local environment, one can try the verity-sdk in a similar environment that a developer developing with verity-sdk would use. This path requires a few more steps, but these steps would be required to build an application with verity-sdk.

### Prerequisites
* `libvdrtools` -- Installation instructions can be found [here](https://gitlab.com/evernym/verity/vdr-tools#installing).

* `Ngrok` -- This is a temporary installation to facilitate early experimentation. Follow the instructions on the [Ngrok website](https://ngrok.com/download).
* `NodeJs v12` -- Follow the instructions on the [NodeJs website](https://nodejs.org/en/).
* `C++ build tools and Python 3.6+` --  NodeJs Indy SDK uses `node-gyp` package that depends on C++ and Python. For more information, follow this [link](https://www.npmjs.com/package/node-gyp).

### Build

Build the app with:
```sh
cd /samples/sdk/oob-with-request-attach
npm install
``` 

### Launch the application
1. **Start `ngrok`**

   The Verity server instance is not running in the same local environment as the example application. The Verity server instance communicates with the SDK via a webhook. This webhook must be accessible by the Verity server instance, but the example application can only start a local webhook endpoint. `ngrok` is used to provide a publicly available endpoint that tunnels to the local webhook endpoint that is started by the Example App. 
   
   See [Ngrok's product page](https://ngrok.com/product) for a more detailed description of what Ngrok is and what it does. 
   
   The example application uses port `4000` by default for the webhook endpoint. So, `ngrok` much be launched pointing to `4000`.
   
   Start `ngrok` command in a separate terminal with
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
   
   The `Forwarding` URL is what must be given to the application during the initial provisioning. For the above output, it would be `https://a74616ad.ngrok.io` but every time `ngrok` is started a new URL will be generated, but it will have the following format:
   
   `https://<id>.ngrok.io`
   
   > **NOTE:** `ngrok` must be left running during the whole time the example application is running.
1. **Start application**
    Start the application with
    ```sh
    cd /samples/sdk/oob-with-request-attach
    node oob-with-request-attach.js
    ```

    The example application should start and perform several provisioning steps.
    After successful provisioning the application will print the output
    ```
    Listening on port 4000
    ```

   At this point, the application is running and ready.
   Visit the address http://localhost:4000 to try out the app.


© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.