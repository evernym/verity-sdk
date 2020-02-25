
# Getting Started with the Verity SDK for Java
## Standing Up a Evaluation Verity Application
To use the verity-sdk, we will need an instance of the Verity Application to interact with. 

We have two means of standing up an instance of the Verity Application:
1. Build and/or run a limited instance of Verity Application in a docker container.
1. Have a member of our Customer Success team provision a small instance of the Verity Application in the cloud.

### Docker
#### Build
Building the Verity Application docker image assumes access to:
1. Internal Evernym Dev Debian repo
1. Internal Evernym Debian public key
1. Sovrin public Debian repo
1. Ubuntu public mirrors

At the root of this repo run this cmd to build the image:
 
```docker build -t verity -f verity/verity.dockerfile verity/```

This should build a new image tagged with

`verity:latest`

#### Run


Docker is very flexible and run many ways. The follow is an example of a `docker` run command to run Verity Application.

```docker run --name verity -it --rm -p 9000:9000 -e VERITY_SEED=NjRkNmM1NzUzMzlmM2YxYjUzMGI4MTZl -e NETWORK=team1  verity```

* `--name verity` - gives the running docker instance the name `verity`
* `-it`  - helps with stdout in the console where it is being run
* `--rm` - will remove the container after exiting
* `-p 9000:9000` - exposes the Verity Application port
* `-e` - enviroment variables
* `verity` - points to the image create previously

**Enviroment Variables**
* VERITY_SEED - Seeds the DID and Verkey used for the verity endpoint
* NETWORK - Selects the Identity Network used by Verity Application
  * team1 - points to the team1 ledger
  * demo - points to the demo ledger
  
The running container uses a script that will guild the user and display details about the running Verity Application.

The Verity Application requires a public DID is on used Identity Network. The script will pause and allow for the User to put the generated DID and its verkey onto the ledger. 

Logs from the Verity Application can be displayed pressing enter when requested after the application has finished starting up.

### Cloud

See your friendly neighborhood Customer Sucess team member for a cloud-based Verity Application.

**TODO** Add information for this step

## Running the Java Example App
The Java Example App is a simple showcase of the Java Verity-SDK.

### Prerequisites

#### Install Libindy
See and follow instructions on [Indy-sdk Github Project page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).

#### Install Ngrok
See and follow instructions on the [Ngrok website](https://ngrok.com/download).

#### Install Maven
See and follow instructions on the [Maven website](http://maven.apache.org/download.cgi)

#### Clone Verity Sdk Repo

```git clone git@gitlab.corp.evernym.com:dev/verity/verity-sdk.git```

#### Repo Requirements:
1. Internal Evernym Dev Maven repo
1. Public Sovrin Maven repo
1. Public Maven Centrol repo

### Build
##### Steps:

1. Move the Example App project folder
  
   `cd verity-sdk/sdk/java-sdk/example`
2. Compile the Example App (using)

   `mvn compile`
   
### Run
#### Steps:
1. Start Ngrok -- Ngrok is used allow the verity application to reach a webhook endpoint that the Example app starts. Be default, the Example App uses port `4000`. So, Ngrok must be started to proxy to that local port. 

   To start `ngrok` use the following command:
   
   ```ngrok http 4000```
   
   This will start `ngrok`, this process will need to be running at the same time as the Example App and endpoint will need to later be given to the Example App. See the *Setup* interaction below.
1. Get and Setup Connect.Me
   The interaction that is simulated in the Example App requires the use of the Connect.me mobile app. 
   
   The Connect.me mobile app can be found on the Apple App Store and Android App Store.
   
   **TODO** Add more instructions for setup of connect.me
1. Launch Example App
   
   `mvn exec:java` 
   
   will start the Example App and begin the simulated interaction.

#### Interactions
The Example App guides the User through a series of interactions as explain below:

1. **Setup**
   
   During setup there are three task being accomplished:
   
   1. Provisioning an Agent
   1. Registering a Webhook
   1. Setting up an Issuer Identity 
   
   When setting up for the frist time, the Example app will require to inputs from the using during this process. These inputs will be asked for by the Example app.
   1. The url for the Verity Application it will be connecting to.
   2. The ngrok url for the webhook that was started earlier. 
   
   Additionally, the Issuer Identity must be written to the ledger. This can be done using the [Sovrin Self-Serve website](https://selfserve.sovrin.org/) for the Sovrin StagingNet. The DID and Verkey will be displayed in the console and they must be transferred accurately to the self-serve site. The Example app will pause and will for this state to be completed. Press enter once the identity is on the ledger.
   
   After the first setup, the context for the verity-sdk is saved to disk and can be reused. 
1. **Connecting**
   
   Connecting is largely automated in the Example App. Incoming messages can be view in the console as they arrive from the Verity Application.
    
   A `QR code` image will be generated and place in the current working directory. This QR code can be scanned by the Connect.me app to finish forming the connection between Verity Application and Connect.me.<!--1. Ask a Committed Answer-->
1. **Write Schema to Ledger**

   Writing the Schema to the Ledger is fully automated in the Example App. Incoming messages can be view in the console as they arrive from the Verity Application.
1. **Write Credential Definition to Ledger**

   Writing the Credential Definition to the Ledger is fully automated in the Example App. Incoming messages can be view in the console as they arrive from the Verity Application.
1. **Issue Degree Credential**
   
   Issuing a credential is automated in the Example App. It will wait for steps to be completed in the Connect.me App asynchronously. The credential should pop up in the Connect.me App and in the Connect.me app it can be accepted.
1. **Request Proof Presentation**
   The request for Proof is automated in the Example App. It will wait for steps to be completed in the Connect.me App asynchronously. The request should pop up in the Connect.me App and in the Connect.me app it can be accepted.
   
After the proof is exchange with the Verity Application, the simulated interaction will be complete and the Example App will terminate.

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.