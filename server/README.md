# Verity 2.0 Server POC
Node.js/Typescript Verity 2.0 Server that uses full agent messaging protocols
THIS IS A DEMO
Companion project to Verity 2.0 POC

## Dev environment setup

### start build watch
watches for changes to the src/ & test/ directories and recompiles in the build directory
**NOTE:** in order to get the server to run with the latest changes in build, this step is *required*
```
$ npm run build:watch
```
### start the test watch suite with jest
starts the test suite to watch for changes in the src/ directory. *Remember* you test the typescript in the src/ 
directory and then run the code in the build directory.
```
$ npm run test:watch
```
## Start the server
starts the server
### with default node
starts the server and will NOT watch for changes
```
$ node build/src/app.js
```
### usage with nodemon
starts the server using nodemon and WILL watch for changes and auto restart the server
**NOTE** nodemon should be installed globably
```
$ npm install -g nodemon
$ nodemon ./build/src/app.js
```

### Running with docker
#### Run build before starting docker
```
$ npm run build:watch 
```
#### Start the docker daemeon
```
$ ./devops/dev/prepare.sh
$ docker-compose up --build
```
#### Run tests inside docker
```
$ docker exec -it verity-server /bin/bash
$ npm run test:watch
```