# Verity SDK

## Overview

The goal of the Verity SDK is to provide easy application integration with the Verity server. 
The Verifiable Credentials data model defines Issuer, Verifier and the Holder. Issuer is an 
organization that creates and issues Verifiable Credentials to individuals, also known as Holders. 
Holders typically have a digital wallet app to store credentials securely and control how those 
credentials are being shared with Verifiers. Verifier is an organization that verifies information 
from the credentials that Holders have stored on their digital wallet app.

With Verity SDK, you can enable issuing or verifying or both functions into your project and interact
with individuals using Connect.Me or some other compatible digital wallet app.

Verity SDK provides methods that enable you to initiate basic SSI protocols such are 
establishing a DID connection between your organization and individuals, issuing a 
Verifiable Credential to individual and requesting and validating Proofs from individuals. 
Besides SSI protocols, Verity SDK provides methods for writing Schemas and 
Credential Definitions to the ledger.

The SDK 
uses `libindy` and Agent-to-Agent protocols to securely authenticate and communicate with the 
Verity server. With the creation of language-specific bindings, developers can quickly integrate 
their backend services into the world of SSI with minimal overhead.

## Architecture

By design, Verity SDK is largely stateless (requiring only a small configuration context 
and a single public/private key pair), which allows applications to orchestrate SSI integrations 
without heavy involvement in the interactions.

## Terminology

The instructions for this SDK use the following terms:

* **Verity Application instance** -- A demo version of the Verity Application instance for testing the SDK

* **Example application** -- A sample application provided by Evernym that demonstrates the basic 
steps involved in various Connect.Me transactions

## Setup 

These are the general steps for setting up the Verity SDK for development:

1. Stand up a Verity Application instance in the cloud. 
2. Download and integrate the language-specific library of your choice.
3. **Optional** -- Run the example application against the Verity server instance.

At this point you will be ready to write and test your application.


<a id="cloud"></a>

## 1. Stand up a Verity Application instance in the cloud

Evernym will stand up a Verity Application instance for you and provision it. 
Contact us for instructions on connecting to it.

### Firewall Rules

Using the Verity SDK requires the following rules in your firewall:

| Direction | Protocol | Port           | MIME Type                |
| --------- | -------- | -------------- | -------------------------|
| Outbound  | HTTP     | 80             | application/octet-stream |
| Outbound  | HTTPS    | 443            | application/octet-stream |
| Inbound   | HTTP     | [user-defined] | application/octet-stream |
| Inbound   | HTTPS    | [user-defined] | application/octet-stream |

---

### Next Step
Getting Started Guide (Guided example application walkthrough)
* [Guide](docs/getting-started/getting-started.md)

Install the language-specific elements of the SDK:
* [Java](sdk/java-sdk/README.md)
* [NodeJs](sdk/nodejs-sdk/README.md)
* [Python](sdk/python-sdk/README.md)


For more information about Evernym products, visit https://www.evernym.com/products/.

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.