# Verity 2.0 Server POC
Node.js/Typescript Verity 2.0 Server that uses full agent messaging protocols
THIS IS A DEMO
Companion project to Verity 2.0 POC

## Setup
Start from server director
```
$ cd server
```

### start build watch
watches for changes to the src/ & test/ directories and recompiles in the build directory
**NOTE:** in order to get the server to run with the latest changes in build, this step is *required*
```
$ npm run build:watch
```
### Start the docker daemeon
Prepare docker files
```
$ ./devops/dev/prepare.sh
```
start docker
```
$ docker-compose up --build
```
### start the test suite insider docker container
starts the test suite to watch for changes in the src/ directory. 

#### launch bash from inside the docker container
```
$ docker exec -it verity-server /bin/bash
```
#### start the test suite
```
$ npm run test:watch
```