# verity-sdk Repo

## Overview

<<<<<<< HEAD
The goal of the Verity SDK is to provide easy application integration with the Verity server. This integration provides the basis for SSI-enabled workflows such as creating connections, issuing credentials, requesting proof, and general pairwise interactions. The SDK uses `libindy` and Agent-to-Agent protocols to securely authenticate and communicate with the Verity server. With the creation of language-specific bindings, developers can quickly integrate their backend services into the world of SSI with minimal overhead.
=======
The goal of the Verity SDK is to provide easy application integration with the Verity server. This integration provides the basis for SSI-enabled workflows such as creating connections, issuing credentials, requesting proof, and general pairwise interactions. The SDK uses `libindy` and Agent-to-Agent protocols to securely authenticate and communicate with the Verity server. With the creation of language-specific bindings, developers can quickly integrate their backend services into the world of SSI with minimal overhead.
## Repo Structure

This repo is divided into two major portions: the SDK and the server. The SDK contains the language-specific bindings for interacting with the Verity server in an idiomatic way. The server is a *mock* Verity server to simplify integration and testing of applications. It runs an in-memory-only service that can be deployed easily for application testing but that lacks many key features needed for production use.

<!--Take time to become familiar with the language-specific SDK of your choice, starting with its README. -->

<a id="setup"></a>

## Setup 

These are the general steps for setting up the Verity SDK for development:

1. Install `libindy` and its Python wrapper (https://github.com/hyperledger/indy-sdk).
2. As the User/UID that will use the SDK, execute the `tools/provisioner/provision_sdk.py` script and save the resulting config file. 
3. Stand up a Verity server instance.
3. Download and integrate the language-specific library of your choice.
4. Run the integration code example against the Verity server instance.
5. Write and test your application, making sure to use the config file from Step 2 during initialization, while also making sure to use the same User/UID from Step 2.

### Next Step

[Stand up a Verity server instance](/docs/Getting-Started/VerityInstance.md)
>>>>>>> a3ca8e971269d9fceb8dcba57de57b320b86ea40
