# Standing Up an Evaluation Verity Application
To use the Verity SDK, you need an instance of the Verity application to interact with. 

There are two ways to stand up an instance of the Verity application:
1. Build or run a limited instance of Verity application in a Docker container.
1. Ask a member of the Evernym Customer Success team to provision a small instance of the Verity application in the cloud.

## Docker
### Build
To build the Verity application Docker image you must have access to:
* The internal Evernym Dev Debian repo
* The internal Evernym Debian public key
* The Sovrin public Debian repo
* Ubuntu public mirrors

At the root of this repo <!--Which repo?--> run this command to build the image:
 
```sh
docker build -t verity -f verity/verity.dockerfile verity/
```

The command builds a new image tagged as `verity:latest`.

### Run


Docker can be run in a variety of ways. The following is an example of a `docker run` command to run the Verity application.

```sh
docker run --name verity -it --rm -p 9000:9000 -e VERITY_SEED=NjRkNmM1NzUzMzlmM2YxYjUzMGI4MTZl -e NETWORK=team1  verity
```

* `--name verity` - gives the running docker instance the name `verity`
* `-it`  - helps with `stdout` in the console where it is being run
* `--rm` - removes the container after exiting
* `-p 9000:9000` - exposes the Verity application port
* `-e` - contains enviroment variables
* `verity` - points to the image created previously

**Enviroment Variables**
* `VERITY_SEED` - Seeds the DID and Verkey used for the Verity endpoint
* `NETWORK` - Selects the Identity Network used by the Verity application
  * `team1` - points to the `team1` ledger
  * `demo` - points to the `demo` ledger
  
The running container uses a script that will guide the user and display details about the running Verity application.

The Verity application requires a public DID to be used on the Identity Network. The script will pause and allow for the user to put the generated DID and its Verkey onto the ledger. The [Sovrin Self-Serve website](https://selfserve.sovrin.org/) can be used if the target network is the Sovrin BuilderNet or Sorvin StagingNet.

Logs from the Verity application can be displayed by pressing **Enter** when requested, after the application has finished starting up.

## Cloud

Speak to a Customer Success team member for a cloud-based Verity application.

<!--**TODO** Add information for this step -->