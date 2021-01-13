# Verity in Docker

To use the Verity SDK, you need an instance of the Verity Application Service (VAS) to interact with. When you specify the Verity application endpoint for your Verity SDK there are two options:
* Use Verity application endpoint provided to you by Evernym
* Start a local Verity Application Service in a docker container (described in this document) and use Verity application endpoint of the started container
 

## Standing up Verity Application Service in a docker container

> **NOTE**: Verity application is available under the Business Source License (https://github.com/evernym/verity/blob/master/LICENSE.txt). Please contact Evernym if you have any questions regarding Verity licensing.

Local Verity application service started in a docker container has several limitations compared to the cloud offering e.g.:
* Local port of the Verity application is assigned a publicly accessible URL (Verity Application endpoint) via the Ngrok tunnel. The Ngrok tunnel expires in 8 hours and Verity container needs to be restarted
* Image uses LevelDB for persistent storage for actor events and is not suitable for clustering

## Starting Verity application in a Docker container

Pull the docker image of the Verity application from Docker hub

```sh
docker pull evernymdev/verity-server-dev:stable
```

Start Verity application with `docker run` command, e.g.
```sh
docker run --name verity -it -p 80:9000 -e VERITY_SEED=NjRkNmM1NzUzMzlmM2YxYjUzMGI4MTZl evernymdev/verity-server-dev:stable
```
Explanations:
* `--name verity` gives the running Docker instance the name `verity`.
* `-it` helps with `stdout` in the console where it is being run.
* `-p 80:9000` maps Verity application port 9000 of the docker container to the host port 80
* `-e` specifies enviroment variables passed to the container
* `evernymdev/verity-server-dev:stable` points to the Verity docker image pulled in the previous step

**Enviroment Variables**

It is possible to specify following environment variables
* `VERITY_SEED` seed based on which Verity DID/Verkey will be generated. If specified, length needs to be exactly 32 characters. If not specified a random one will be used
* `NETWORK` selects the Identity Network used by the Verity application. If not specified `demo` will be used
  * `demo` points to the `demo` ledger (Sovrin Staging Net)
  * `prod` points to the `prod` ledger (Sovrin Main Net)
  * `team1` points to the `team1` ledger.
* `HOST_ADDRESS` specifies public URL of the docker host. Use this option if you have capabilities so start docker container on a public infrastructure. If not specified, Verity application port 9000 will be exposed on a public URL endpoint via Ngrok tunnel

**Entrypoint**
The running container uses the entrypoint script that will guide the user and display details about the running Verity application.
The Docker is NOT meant to be run in detached mode (-d option), since it requires user input at certain stages, for instance:
* user needs to press ENTER once Verity DID/Verkey have been written to the ledger externally for "prod" and "team1" enviroments (for "demo" this is not needed since write of DID/Verkey is automated)
* user needs to press ENTER to start tailing of Verity logs once the application has started (`Press enter to start tailing log...` is printed in output of the terminal with `docker run` command)

**Restarting container**

If you exit the Verity container (by pressing CTRL+C) you can start it again with
```sh
docker start -ai verity
```
The container will preserve it's state across restarts.
Restart might be needed if Ngrok is being used (i.e. HOST_ADDRESS env variable was not specified in `docker run` command) since the Ngrok tunnel will expire in 8 hours. When the stopped Verity container is started again new Ngrok tunnel will be created and new Verity Application Endpoint will be printed on the screen.


