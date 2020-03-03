# verity-sdk Repo

## Overview

The goal of the Verity SDK is to provide easy application integration with the Verity server. This integration provides the basis for SSI-enabled workflows such as creating connections, issuing credentials, requesting proof, and general pairwise interactions. The SDK uses `libindy` and Agent-to-Agent protocols to securely authenticate and communicate with the Verity server. With the creation of language-specific bindings, developers can quickly integrate their backend services into the world of SSI with minimal overhead.

## Repo Structure

This repo is divided into two major portions: the SDK and the server. The SDK contains the language-specific bindings for interacting with the Verity server in an idiomatic way. The server is a *mock* Verity server to simplify integration and testing of applications. It runs an in-memory-only service that can be deployed easily for application testing but that lacks many key features needed for production use.

## Architecture

By design, Verity SDK is stateless, which gives control of the library-provided APIs back to the consumer. This architecture has significant implications for the design of the library: namely, it will function as a helper to the library for ease of use with Verity, but it will not interact with the Verity Agent itself. 

<!--We need something here on what the setup looks like in general, what the component parts are, what they generally will be accomplishing-->

## Setup 

These are the general steps for setting up the Verity SDK for development:

1. Stand up a Verity server instance in the cloud. 
3. Download and integrate the language-specific library of your choice.
4. Run the integration code example against the Verity server instance.
5. Write and test your application.

<a id="cloud"></a>

## 1. Stand up a Verity server instance in the cloud

<!--need info from Trev on this-->

### Next Step

Install the language-specific elements of the SDK:
* [Java](/docs/Getting-Started/java/README.md)
* [NodeJs](/docs/Getting-Started/nodejs/README.md)
* [Python](/docs/Getting-Started/python/README.md)

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.