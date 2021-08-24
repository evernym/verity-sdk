### NodeJs Example Application
An example application that demonstrate simple SSI use-cases using the NodeJs Verity SDK. 

See [Getting Started](../../../docs/getting-started/getting-started.md) guide for a guided tutorial that makes use of this example application.  

## Prerequisites
Install the following items:
* `libindy` -- Install a stable version. Follow the instructions on the 
[indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).

>**NOTE:** 
>
>On **Windows** you'll also need to create a system variable LD_LIBRARY_PATH with a value of a path to Libindy /lib directory which contains dll file.

* `Ngrok` -- This is a temporary installation to facilitate early experimentation. Follow the instructions on the [Ngrok website](https://ngrok.com/download).
* `NodeJs v12` -- Follow the instructions on the [NodeJs website](https://nodejs.org/en/).
* `C++ build tools and Python 3.6+` --  NodeJs Indy SDK uses `node-gyp` package that depends on C++ and Python. For more information, follow this [link](https://www.npmjs.com/package/node-gyp).


### Install dependencies
```sh
npm install
```

### Run example application
```sh
node example.js
```