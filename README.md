# verity-sdk Repo

## Overview

The goal of the Verity SDK is to provide easy application integration with the Verity server. This integration provides the basis for SSI (self-sovereign identity)-enabled workflows such as creating connections, issuing credentials, requesting proof, and general pairwise interactions. The SDK uses `libindy` and Agent-to-Agent protocols to securely authenticate and communicate with the Verity server. With the creation of language-specific bindings, developers can quickly integrate their backend services into the world of SSI with minimal overhead.

## Architecture

By design, Verity SDK is largely stateless (requiring only a small set of configuration contexts and a single public/private key), which allows using applications to orchestrate SSI integrations without heavy involvement in the interactions.

## Terminology

The instructions for this SDK use the following terms:

* **Customer integration code** &#8212; The Verity application that you create using the SDK

* **Example application** &#8212; An sample application provided by Evernym that demonstrates the basic steps involved in various Connect.Me transactions

## Setup 

These are the general steps for setting up the Verity SDK for development:

1. Stand up a Verity server instance in the cloud. 
3. Download and integrate the language-specific library of your choice.
4. **Optional** &#8212; Run the example application against the Verity server instance.

At this point you will be ready to write and test your application.


<a id="cloud"></a>

## 1. Stand up a Verity server instance in the cloud
<!--need info from Trev on this-->[TBD]

## Firewall Rules

Using the Verity server instance requires the following rules in your firewall:

| Direction | Protocol | Port | MIME Type |
| --- | --- | --- | --- |
| Outbound | HTTP | 80 | application/octet-stream |
| Outbound | HTTPS | 443 | application/octet-stream |
| Inbound | HTTP | [user-defined] | application/octet-stream |
| Inbound | HTTPS | [user-defined] | application/octet-stream |


### Next Step

Install the language-specific elements of the SDK:
* [Java](/docs/Getting-Started/java/README.md)
* [NodeJs](/docs/Getting-Started/nodejs/README.md)
* [Python](/docs/Getting-Started/python/README.md)

© 2013&#8211;2020, ALL RIGHTS RESERVED, EVERNYM INC.