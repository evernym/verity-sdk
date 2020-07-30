# Verity SDK

## Overview

The goal of the Verity SDK is to provide easy application integration with the Verity server. 
The Verifiable Credentials data model defines Issuer, Verifier, and Holder. The Issuer is an 
organization that creates and issues Verifiable Credentials to individuals, also known as Holders. 
Holders typically have a digital wallet app to store credentials securely and control how those 
credentials are being shared with Verifiers. Verifier is an organization that verifies information 
from the credentials that Holders have stored on their digital wallet app.

With Verity SDK, you can enable issuing or verifying or both functions into your project and interact
with individuals using Connect.Me or some other compatible digital wallet app.

Verity SDK provides methods that enable you to initiate basic SSI protocols such are 
establishing a DID connection between your organization and individuals, issuing a 
Verifiable Credential to holding individuals, and requesting verifiable presentation of these Credentials from holding individuals. 
Besides SSI protocols, Verity SDK provides methods for writing Schemas and 
Credential Definitions to the ledger.

The SDK uses `libindy` and Agent-to-Agent protocols to securely authenticate and communicate with the 
Verity server. With the creation of language-specific bindings, developers can quickly integrate 
their backend services into the world of SSI with minimal overhead.

## Architecture

By design, Verity SDK is largely stateless (requiring only a small configuration context 
and a single public/private key pair), which allows applications to orchestrate SSI integrations 
without heavy involvement in the interactions.

## Terminology

The instructions for this SDK use the following terms:

* **Verity Application agent** -- An Identity agent run on Evernym's Verity Application. This agent is controlled by the Verity SDK.

* **Example application** -- A sample application provided by Evernym that demonstrates the basic 
steps involved in various Connect.Me transactions

## First Steps 

These are the general steps for getting going with the Verity SDK:

1. Get an Agent on Evernym's Verity Application. 
2. Integrate the language-specific Verity SDK library.
3. **Optional** -- Walk-through the 'Getting Started' guided tutorial.

## 1. Get an Agent on Evernym's Verity Application
Verity SDK requires access to an agent running on Evernym's Verity Application. Contact Evernym to get access to an Agent.

### Firewall Rules

Using the Verity SDK requires the following rules in your firewall:

| Direction | Protocol | Port           | MIME Type                |
| --------- | -------- | -------------- | -------------------------|
| Outbound  | HTTP     | 80             | application/octet-stream |
| Outbound  | HTTPS    | 443            | application/octet-stream |
| Inbound   | HTTP     | [user-defined] | application/octet-stream |
| Inbound   | HTTPS    | [user-defined] | application/octet-stream |

---
## 2. Integrate the language-specific Verity SDK library
The Verity SDK comes is several language-specific libraries. See the respective `README` documents to get the latest version, read the API documentation and other information:
* [Java](sdk/java-sdk/README.md)
* [NodeJs](sdk/nodejs-sdk/README.md)
* [Python](sdk/python-sdk/README.md)

## 3. **Optional** -- Walk-through the 'Getting Started' guided tutorial
The 'Getting Started' guide is a guided tutorial that walks-through a couple of simple SSI use-cases using Verity and the Verity SDK. The guide can be done with `Docker` to simplify setting up an environment or can be done using a local developer environment.

See the [Getting Started](docs/getting-started/getting-started.md) guide documentation.

---
For more information about Evernym products, visit https://www.evernym.com/products/.

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.