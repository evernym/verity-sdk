# Verity SDK

## Overview

The goal of the Verity SDK is to provide easy application integration with the Verity server. This 
integration provides the basis for SSI-enabled workflows such as creating 
connections, issuing credentials, requesting proof, and general pairwise interactions. The SDK 
uses `libindy` and Agent-to-Agent protocols to securely authenticate and communicate with the 
Verity server. With the creation of language-specific bindings, developers can quickly integrate 
their backend services into the world of SSI with minimal overhead.

## Architecture

By design, Verity SDK is largely stateless (requiring only a small configuration context 
and a single public/private key pair), which allows applications to orchestrate SSI integrations 
without heavy involvement in the interactions.

## Terminology

The instructions for this SDK use the following terms:

* **Verity server instance** -- A demo version of the Verity server for testing the SDK

* **Example application** -- A sample application provided by Evernym that demonstrates the basic 
steps involved in various Connect.Me transactions

## Setup 

These are the general steps for setting up the Verity SDK for development:

1. Stand up a Verity server instance in the cloud. 
2. Download and integrate the language-specific library of your choice.
3. **Optional** -- Run the example application against the Verity server instance.

At this point you will be ready to write and test your application.


<a id="cloud"></a>

## 1. Stand up a Verity server instance in the cloud

Evernym will stand up a Verity server instance for you and provision it. 
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

Install the language-specific elements of the SDK:
* [Java](/docs/Getting-Started/java/README.md)
* [NodeJs](/docs/Getting-Started/nodejs/README.md)
* [Python](/docs/Getting-Started/python/README.md)

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.