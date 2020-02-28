# Standing Up a Evaluation Verity Application
To use the verity-sdk, we will need an instance of the Verity Application to interact with. 

We have two means of standing up an instance of the Verity Application:
1. Build and/or run a limited instance of Verity Application in a docker container.
1. Have a member of our Customer Success team provision a small instance of the Verity Application in the cloud.

## Docker
### Build
Building the Verity Application docker image assumes access to:
1. Internal Evernym Dev Debian repo
1. Internal Evernym Debian public key
1. Sovrin public Debian repo
1. Ubuntu public mirrors

At the root of this repo run this cmd to build the image:
 
```docker build -t verity -f verity/verity.dockerfile verity/```

This should build a new image tagged with

`verity:latest`

### Run


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

The Verity Application requires a public DID is on used Identity Network. The script will pause and allow for the **User** to put the generated DID and its verkey onto the ledger. The [Sovrin Self-Serve website](https://selfserve.sovrin.org/) can be used it the target network is the Sovrin BuilderNet or Sorvin StagingNet.

Logs from the Verity Application can be displayed pressing enter when requested after the application has finished starting up.

## Cloud

See your friendly neighborhood Customer Success team member for a cloud-based Verity Application.

**TODO** Add information for this step